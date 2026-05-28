package com.pharmacy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pharmacy.entity.Medicine;
import com.pharmacy.vo.ClientMedicineVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MedicineMapper extends BaseMapper<Medicine> {
    @Select("""
            <script>
            SELECT m.*,
                   (
                       SELECT GROUP_CONCAT(DISTINCT COALESCE(s.short_name, s.supplier_name) ORDER BY s.supplier_id SEPARATOR '、')
                       FROM stock st
                       LEFT JOIN supplier s ON st.supplier_id = s.supplier_id
                       WHERE st.med_id = m.med_id
                         AND st.supplier_id IS NOT NULL
                   ) AS supplier_name
            FROM medicine m
            <where>
                <if test="medName != null and medName != ''">
                    AND (m.med_name LIKE CONCAT('%', #{medName}, '%')
                         OR m.med_alias LIKE CONCAT('%', #{medName}, '%'))
                </if>
                <if test="medType != null and medType != ''">
                    AND m.med_type = #{medType}
                </if>
                <if test="status != null">
                    AND m.status = #{status}
                </if>
                <if test="status == null">
                    AND m.status = 1
                </if>
            </where>
            ORDER BY m.med_id ASC
            </script>
            """)
    Page<Medicine> selectMedicinePage(Page<Medicine> page,
                                      @Param("medName") String medName,
                                      @Param("medType") String medType,
                                      @Param("status") Integer status);

    @Select("""
            SELECT m.*,
                   (
                       SELECT GROUP_CONCAT(DISTINCT COALESCE(s.short_name, s.supplier_name) ORDER BY s.supplier_id SEPARATOR '、')
                       FROM stock st
                       LEFT JOIN supplier s ON st.supplier_id = s.supplier_id
                       WHERE st.med_id = m.med_id
                         AND st.supplier_id IS NOT NULL
                   ) AS supplier_name
            FROM medicine m
            WHERE m.med_id = #{id}
            """)
    Medicine selectMedicineById(@Param("id") Integer id);

    @Update("""
            <script>
            UPDATE medicine
            <set>
                <if test="medicine.medName != null">med_name = #{medicine.medName},</if>
                <if test="medicine.medAlias != null">med_alias = #{medicine.medAlias},</if>
                <if test="medicine.medType != null">med_type = #{medicine.medType},</if>
                <if test="medicine.spec != null">spec = #{medicine.spec},</if>
                <if test="medicine.unit != null">unit = #{medicine.unit},</if>
                <if test="medicine.dosageForm != null">dosage_form = #{medicine.dosageForm},</if>
                <if test="medicine.manufacturer != null">manufacturer = #{medicine.manufacturer},</if>
                <if test="medicine.approvalNo != null">approval_no = #{medicine.approvalNo},</if>
                <if test="medicine.retailPrice != null">retail_price = #{medicine.retailPrice},</if>
                <if test="medicine.purchasePrice != null">purchase_price = #{medicine.purchasePrice},</if>
                <if test="medicine.isRx != null">is_rx = #{medicine.isRx},</if>
                <if test="medicine.status != null">status = #{medicine.status},</if>
                <if test="medicine.remark != null">remark = #{medicine.remark},</if>
                update_time = NOW()
            </set>
            WHERE med_id = #{medicine.medId}
            </script>
            """)
    int updateMedicineById(@Param("medicine") Medicine medicine);

    @Select("""
            <script>
            SELECT m.med_id AS id,
                   m.med_name AS name,
                   m.med_alias AS med_alias,
                   m.med_type AS med_type,
                   m.spec AS spec,
                   m.unit AS unit,
                   m.dosage_form AS dosage_form,
                   m.manufacturer AS manufacturer,
                   m.approval_no AS approval_no,
                   m.retail_price AS retail_price,
                   m.is_rx AS is_rx,
                   COALESCE(sa.stock_count, 0) AS stock_count,
                   CASE
                       WHEN COALESCE(sa.stock_count, 0) &lt;= 0 THEN '无货'
                       WHEN COALESCE(sa.stock_count, 0) &lt;= COALESCE(sa.stock_min, 0) THEN '库存较少'
                       ELSE '有货'
                   END AS stock_status
            FROM medicine m
            LEFT JOIN (
                SELECT med_id, SUM(stock_num) AS stock_count, MAX(stock_min) AS stock_min
                FROM stock
                WHERE status = 1
                GROUP BY med_id
            ) sa ON sa.med_id = m.med_id
            <where>
                m.status = 1
                <if test="keyword != null and keyword != ''">
                    AND (m.med_name LIKE CONCAT('%', #{keyword}, '%')
                         OR m.med_alias LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="type != null and type != ''">
                    AND m.med_type = #{type}
                </if>
                <if test="dosageForm != null and dosageForm != ''">
                    AND m.dosage_form = #{dosageForm}
                </if>
                <if test="isRx != null">
                    AND m.is_rx = #{isRx}
                </if>
                <if test="stockStatus != null and stockStatus != ''">
                    <choose>
                        <when test="stockStatus == '有货'">
                            AND COALESCE(sa.stock_count, 0) &gt; COALESCE(sa.stock_min, 0)
                        </when>
                        <when test="stockStatus == '库存较少'">
                            AND COALESCE(sa.stock_count, 0) &gt; 0
                            AND COALESCE(sa.stock_count, 0) &lt;= COALESCE(sa.stock_min, 0)
                        </when>
                        <when test="stockStatus == '无货'">
                            AND COALESCE(sa.stock_count, 0) &lt;= 0
                        </when>
                    </choose>
                </if>
            </where>
            ORDER BY m.med_id ASC
            </script>
            """)
    Page<ClientMedicineVO> selectClientMedicinePage(Page<ClientMedicineVO> page,
                                                    @Param("keyword") String keyword,
                                                    @Param("type") String type,
                                                    @Param("dosageForm") String dosageForm,
                                                    @Param("isRx") Integer isRx,
                                                    @Param("stockStatus") String stockStatus);

    @Select("""
            SELECT m.med_id AS id,
                   m.med_name AS name,
                   m.med_alias AS med_alias,
                   m.med_type AS med_type,
                   m.spec AS spec,
                   m.unit AS unit,
                   m.dosage_form AS dosage_form,
                   m.manufacturer AS manufacturer,
                   m.approval_no AS approval_no,
                   m.retail_price AS retail_price,
                   m.is_rx AS is_rx,
                   COALESCE(sa.stock_count, 0) AS stock_count,
                   CASE
                       WHEN COALESCE(sa.stock_count, 0) <= 0 THEN '无货'
                       WHEN COALESCE(sa.stock_count, 0) <= COALESCE(sa.stock_min, 0) THEN '库存较少'
                       ELSE '有货'
                   END AS stock_status
            FROM medicine m
            LEFT JOIN (
                SELECT med_id, SUM(stock_num) AS stock_count, MAX(stock_min) AS stock_min
                FROM stock
                WHERE status = 1
                GROUP BY med_id
            ) sa ON sa.med_id = m.med_id
            WHERE m.status = 1
              AND m.med_id = #{id}
            """)
    ClientMedicineVO selectClientMedicineById(@Param("id") Integer id);
}
