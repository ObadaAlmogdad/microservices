# Course Service API Documentation

## Authentication
- جميع النقاط تتطلب JWT في الهيدر:
  - Authorization: Bearer <jwt_token>

## Endpoints

### الدوال العامة (Public)
- `GET /api/courses` : عرض جميع الكورسات
- `GET /api/courses/{id}` : عرض تفاصيل كورس
- `GET /api/courses/approved` : عرض الكورسات المعتمدة
- `GET /api/courses/category/{category}` : عرض الكورسات حسب التصنيف
- `GET /api/courses/level/{level}` : عرض الكورسات حسب المستوى

### للمدرب فقط (TRAINER)
- `POST /api/courses` : إنشاء كورس جديد
- `PUT /api/courses/{id}/publish` : نشر كورس
- `PUT /api/courses/{id}` : تعديل كورس (أيضًا للأدمن)
- `DELETE /api/courses/{id}` : حذف كورس (أيضًا للأدمن)

### للأدمن فقط (ADMIN)
- `PUT /api/courses/{id}/approve` : الموافقة على كورس
- `PUT /api/courses/{id}/reject` : رفض كورس
- `GET /api/courses/pending` : عرض الكورسات المعلقة
- `GET /api/courses/rejected` : عرض الكورسات المرفوضة

### نقاط الاشتراك (LEARNER فقط)
- `POST /api/enrollments/enroll` : الاشتراك في كورس (مع الدفع)

```
POST /api/courses
{
  "title": "Java Basics",
  "description": "Learn Java from scratch",
  "price": 100,
  "trainerId": 2,
  "category": "Programming",
  "level": "BEGINNER"
}
```

### نشر كورس (مدرب)
```
PUT /api/courses/1/publish
```

### الموافقة على كورس (أدمن)
```
PUT /api/courses/1/approve
```

### الاشتراك في كورس (متعلم)
```
POST /api/enrollments/enroll?courseId=1&learnerId=5
(مع إرسال Authorization في الهيدر)
```

## ملاحظات
- جميع الاستجابات تأتي بصيغة موحدة:
```
{
  "success": true,
  "message": "Course created",
  "data": { ... }
}
```
- في حال الخطأ:
```
{
  "success": false,
  "message": "سبب الخطأ"
}
``` 