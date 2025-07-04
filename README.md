# نظام التعليم الإلكتروني - Microservices Architecture

## نظرة عامة
هذا النظام عبارة عن منصة تعليم إلكتروني مبنية على بنية الخدمات الميكروية (Microservices Architecture) تهدف إلى تسهيل عملية التعليم من خلال إدارة المستخدمين، نشر الدورات التعليمية، وإدارة عمليات الاشتراك والتقييمات.

## بنية النظام

### الخدمات المكونة للنظام:

1. **Eureka Server (Service Discovery)** - Port 8761
   - خدمة اكتشاف الخدمات وتسجيلها
   - تمكن الخدمات من العثور على بعضها البعض

2. **API Gateway** - Port 8083
   - بوابة موحدة لجميع الطلبات
   - توجيه الطلبات إلى الخدمات المناسبة
   - تطبيق Load Balancing

3. **User Service** - Port 8081
   - إدارة المستخدمين (مسؤولين، مدربين، متعلمين)
   - تسجيل المستخدمين وإدارة حساباتهم

4. **Course Service** - Port 8082
   - إدارة الدورات التعليمية
   - إدارة عمليات الاشتراك والتقدم
   - نظام الموافقة على الدورات

5. **Payment Service** - Port 8084
  - ادارة عمليات الدفع و الشحن و الاسترداد

6. **Exam Service** - Port 8085
  - ادارة للامتحانات و تفاصيلها


## المتطلبات

- Java 17
- Maven
- MySQL Database
- Spring Boot 3.5.3
- Spring Cloud 2025.0.0

## كيفية التشغيل

### 1. إعداد قاعدة البيانات
```sql
CREATE DATABASE elearning;
```

### 2. تشغيل الخدمات بالترتيب التالي:

#### أ. تشغيل Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
- الوصول إلى لوحة التحكم: http://localhost:8761

#### ب. تشغيل User Service
```bash
cd user-service
mvn spring-boot:run
```

#### ج. تشغيل Course Service
```bash
cd course-service
mvn spring-boot:run
```

#### د. تشغيل API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

## API Endpoints
  -   postman collection تم اضافة 
  -  :) يمكنك ان تجدها في في خذا المستودع 


## الميزات المطبقة

### 1. Service Discovery
- استخدام Eureka Server لاكتشاف الخدمات
- تسجيل الخدمات تلقائياً
- العثور على الخدمات ديناميكياً

### 2. API Gateway
- بوابة موحدة لجميع الطلبات
- توجيه الطلبات حسب المسار
- تطبيق Load Balancing

### 3. Load Balancing
- توزيع الحمل بين الخدمات
- استخدام `lb://` في Gateway
- تكامل مع Service Discovery

### 4. Communication Between Services
- استخدام OpenFeign للتواصل بين الخدمات
- عنونة ديناميكية للخدمات
- معالجة حالات الفشل

### 5. Error Handling
- معالجة حالات فشل الاتصال
- استجابة مناسبة للأخطاء
- Timeout handling

## أدوار المستخدمين

### Admin (مسؤول)
- إضافة مدربين جدد
- الموافقة على الدورات الجديدة
- إدارة جميع المستخدمين

### Trainer (مدرب)
- إنشاء دورات جديدة
- إدارة دوراته
- متابعة تقدم المتعلمين

### Learner (متعلم)
- التسجيل في النظام
- استعراض الدورات المتاحة
- الاشتراك في الدورات
- متابعة تقدمه


### 1. الأدوار والصلاحيات
- كل مستخدم لديه دور (ADMIN, TRAINER, LEARNER).
- يتم التحقق من الدور يدويًا في الـ API.

### 2. التوكن (Token)
- عند تسجيل الدخول، يتم إرجاع توكن (jwt) يجب إرساله في كل طلب محمي.
- التوكن يحدد هوية المستخدم وصلاحياته.

### 3. محفظة النقود
- تمت إضافة حقل `wallet` للمستخدم.
- يمكن تحديد رصيد المحفظة عند التسجيل أو تعديله لاحقًا.

### 4. الدفع عند الاشتراك بالكورس
- عند الاشتراك بكورس، يتم خصم سعر الكورس من محفظة المستخدم.
- إذا لم يكن هناك رصيد كافٍ، يتم رفض الاشتراك.

### 5. منطق enrollments
- الاشتراك (enrollment) يعني دفع واشتراك بالكورس معًا.
- يتم تخزين المبلغ المدفوع في حقل `amountPaid` داخل Enrollment.

### 6. امتحانات الكورسات
- تمت إضافة خدمة امتحانات (Exam Service):
  - كل كورس يمكن أن يكون له امتحان.
  - كل امتحان له أسئلة.
  - الطلاب يرسلون إجاباتهم.
  - المدرب يقيم الإجابات ويعطي درجة.
- فقط المدرب أو الأدمن يمكنه إضافة امتحان أو سؤال أو تقييم الإجابات.

---

## مثال على استخدام التوكن
- بعد تسجيل الدخول:
  ```json
  { "token": "...", "role": "LEARNER" }
  ```
- يجب إرسال التوكن في الهيدر:
  ```http
  Authorization: <token>
  ```
- ويجب إرسال الدور (Role) في الهيدر عند التعامل مع امتحانات الكورسات:
  ```http
  Role: TRAINER
  ```

## مثال على الاستخدام

### 1. إنشاء مدرب جديد
```bash
curl -X POST http://localhost:8083/api/users/admin/add-trainer \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "أحمد محمد",
    "email": "ahmed@example.com",
    "password": "password123"
  }'
```

### 2. إنشاء دورة جديدة
```bash
curl -X POST http://localhost:8083/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "title": "مقدمة في البرمجة",
    "description": "دورة تعليمية في أساسيات البرمجة",
    "price": 99.99,
    "trainerId": 1,
    "category": "برمجة",
    "duration": 20,
    "level": "BEGINNER"
  }'
```

### 3. الموافقة على دورة
```bash
curl -X PUT http://localhost:8083/api/courses/1/approve
```

### 4. الاشتراك في دورة
```bash
curl -X POST "http://localhost:8083/api/enrollments/enroll?courseId=1&learnerId=2"
```

## ملاحظات مهمة

1. تأكد من تشغيل MySQL قبل تشغيل الخدمات
2. تأكد من تشغيل Eureka Server أولاً
3. جميع الخدمات تستخدم نفس قاعدة البيانات `elearning`
4. يمكن الوصول إلى Eureka Dashboard على http://localhost:8761
5. جميع الطلبات تمر عبر API Gateway على http://localhost:8083 