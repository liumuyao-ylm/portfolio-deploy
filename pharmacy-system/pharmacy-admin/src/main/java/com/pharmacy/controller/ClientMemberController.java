package com.pharmacy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.common.Result;
import com.pharmacy.dto.ClientMemberLoginDTO;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.SaleItem;
import com.pharmacy.entity.SaleOrder;
import com.pharmacy.service.CustomerService;
import com.pharmacy.service.MedicineService;
import com.pharmacy.service.SaleItemService;
import com.pharmacy.service.SaleOrderService;
import com.pharmacy.vo.ClientMemberOrderItemVO;
import com.pharmacy.vo.ClientMemberOrderVO;
import com.pharmacy.vo.ClientMemberProfileVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client/member")
public class ClientMemberController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SaleOrderService saleOrderService;

    @Autowired
    private SaleItemService saleItemService;

    @Autowired
    private MedicineService medicineService;

    @PostMapping("/login")
    public Result<ClientMemberProfileVO> login(@Valid @RequestBody ClientMemberLoginDTO dto) {
        Customer customer = findActiveMemberByPhone(dto.getPhone());
        if (customer == null) {
            return Result.error(404, "未找到该手机号对应的会员");
        }
        return Result.success(toProfile(customer));
    }

    @GetMapping("/profile")
    public Result<ClientMemberProfileVO> profile(@RequestParam Integer customerId) {
        Customer customer = customerService.getById(customerId);
        if (!isActiveMember(customer)) {
            return Result.error(404, "未找到会员资料");
        }
        return Result.success(toProfile(customer));
    }

    @GetMapping("/page")
    public Result<Page<ClientMemberProfileVO>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "1") Integer size) {
        Page<Customer> page = new Page<>(current, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getIsMember, 1)
                .eq(Customer::getStatus, 1)
                .orderByAsc(Customer::getCustId);
        Page<Customer> customerPage = customerService.page(page, wrapper);

        Page<ClientMemberProfileVO> result = new Page<>(
                customerPage.getCurrent(),
                customerPage.getSize(),
                customerPage.getTotal());
        result.setRecords(customerPage.getRecords().stream()
                .map(this::toProfile)
                .collect(Collectors.toList()));
        return Result.success(result);
    }

    @GetMapping("/orders")
    public Result<List<ClientMemberOrderVO>> orders(@RequestParam Integer customerId) {
        Customer customer = customerService.getById(customerId);
        if (!isActiveMember(customer)) {
            return Result.error(404, "未找到会员资料");
        }

        LambdaQueryWrapper<SaleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaleOrder::getCustId, customerId)
                .orderByDesc(SaleOrder::getCreateTime);
        List<ClientMemberOrderVO> orders = saleOrderService.list(wrapper).stream()
                .map(this::toOrder)
                .collect(Collectors.toList());
        return Result.success(orders);
    }

    private Customer findActiveMemberByPhone(String phone) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getPhone, phone)
                .eq(Customer::getIsMember, 1)
                .eq(Customer::getStatus, 1)
                .last("LIMIT 1");
        return customerService.getOne(wrapper, false);
    }

    private boolean isActiveMember(Customer customer) {
        return customer != null
                && Objects.equals(customer.getIsMember(), 1)
                && Objects.equals(customer.getStatus(), 1);
    }

    private ClientMemberProfileVO toProfile(Customer customer) {
        ClientMemberProfileVO vo = new ClientMemberProfileVO();
        vo.setCustomerId(customer.getCustId());
        vo.setName(customer.getCustName());
        vo.setPhone(maskPhone(customer.getPhone()));
        vo.setMemberLevel(customer.getMemberLevel());
        vo.setTotalConsume(customer.getTotalConsume());
        vo.setBirthday(customer.getBirthday());
        vo.setAddress(customer.getAddress());
        vo.setRemark(customer.getRemark());
        vo.setStatusText(Objects.equals(customer.getStatus(), 1) ? "正常" : "停用");
        return vo;
    }

    private ClientMemberOrderVO toOrder(SaleOrder order) {
        LambdaQueryWrapper<SaleItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(SaleItem::getOrderId, order.getOrderId());
        List<ClientMemberOrderItemVO> items = saleItemService.list(itemWrapper).stream()
                .map(this::toOrderItem)
                .collect(Collectors.toList());

        ClientMemberOrderVO vo = new ClientMemberOrderVO();
        vo.setOrderId(order.getOrderId());
        vo.setCreateTime(order.getCreateTime());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setPayType(order.getPayType());
        vo.setOrderTypeText(Objects.equals(order.getOrderType(), 1) ? "线下购药" : "线上订单");
        vo.setPayStatusText(payStatusText(order.getPayStatus()));
        vo.setItems(items);
        vo.setSummary(buildSummary(items));
        return vo;
    }

    private ClientMemberOrderItemVO toOrderItem(SaleItem item) {
        Medicine medicine = medicineService.getById(item.getMedId());
        ClientMemberOrderItemVO vo = new ClientMemberOrderItemVO();
        vo.setItemId(item.getItemId());
        vo.setMedicineId(item.getMedId());
        vo.setMedicineName(medicine == null ? "药品已下架" : medicine.getMedName());
        vo.setQuantity(item.getQuantity());
        vo.setUnitPrice(item.getUnitPrice());
        vo.setTotalPrice(item.getTotalPrice());
        return vo;
    }

    private String buildSummary(List<ClientMemberOrderItemVO> items) {
        if (items == null || items.isEmpty()) {
            return "暂无明细";
        }
        String firstName = items.get(0).getMedicineName();
        if (items.size() == 1) {
            return firstName;
        }
        return firstName + "等 " + items.size() + " 种药品";
    }

    private String payStatusText(Integer payStatus) {
        if (Objects.equals(payStatus, 1)) {
            return "已支付";
        }
        if (Objects.equals(payStatus, 2)) {
            return "已退款";
        }
        return "未支付";
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
