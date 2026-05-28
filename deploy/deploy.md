# Deploy Guide

## 1. Install Dependencies

Install JDK 17, Maven, Node.js, MySQL 8, Nginx and Git on the server.

## 2. Clone Repository

```bash
cd /opt
git clone git@github.com:liumuyao-ylm/portfolio-deploy.git
cd portfolio-deploy
```

## 3. Initialize Database

```bash
mysql -u root -p < pharmacy-system/docs/pms_db.sql
```

The SQL file contains the database schema and only the required initial users.

## 4. Configure Backend Environment

```bash
export DB_URL='jdbc:mysql://127.0.0.1:3306/pms_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false'
export DB_USERNAME='root'
export DB_PASSWORD='your_mysql_password'
```

For production, configure these variables in systemd. See `deploy/systemd/pharmacy-admin.service.sample`.

## 5. Build And Start Backend

```bash
cd /opt/portfolio-deploy/pharmacy-system/pharmacy-admin
mvn package
java -jar target/pharmacy-admin-1.0.0.jar
```

## 6. Build Frontends

```bash
cd /opt/portfolio-deploy/pharmacy-system/pharmacy-ui
npm install
npm run build

cd /opt/portfolio-deploy/pharmacy-system/pharmacy-client
npm install
npm run build
```

## 7. Configure Nginx

Copy `deploy/nginx.sample.conf` to the Nginx site config directory, adjust paths if needed, then enable it.

```bash
nginx -t
systemctl reload nginx
```

The sample config uses:

- `www.yaoliumu.me` for `portfolio-site`
- `admin.yaoliumu.me` for `pharmacy-ui/dist`
- `client.yaoliumu.me` for `pharmacy-client/dist`
- `/api/` proxying to `127.0.0.1:8080`

## 8. Configure HTTPS

Use Certbot or another certificate manager to issue certificates for:

- `www.yaoliumu.me`
- `admin.yaoliumu.me`
- `client.yaoliumu.me`

After HTTPS configuration, run `nginx -t` and reload Nginx again.
