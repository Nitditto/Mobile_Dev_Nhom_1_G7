# Mobile_Dev_Nhom_1_G7 — Ứng dụng quản lý & đặt lịch học sinh viên

Android native: **Kotlin · XML Views + ViewBinding · Room · Material 3**. minSdk 24, targetSdk 36.

## Chạy project

1. Mở thư mục này bằng Android Studio (JDK 17+), chờ Gradle sync.
2. Run cấu hình `app`, hoặc dùng dòng lệnh: `./gradlew assembleDebug`.

Tài khoản demo (được seed khi tạo DB lần đầu):

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Student | `sv01` | `123456` |

## Cấu trúc

```
app/src/main/java/com/nhom1/studyschedule/
├── StudyScheduleApp.kt        # Application: giữ database + session
├── data/
│   ├── AppDatabase.kt         # Room DB + seed data
│   ├── SessionManager.kt      # Lưu phiên đăng nhập (SharedPreferences)
│   ├── entity/                # Account, Student, Subject, StudySchedule, ScheduleItem
│   └── dao/                   # CRUD, tìm kiếm, kiểm tra trùng lịch, truy vấn theo khoảng ngày
├── ui/
│   ├── login/                 # Đăng nhập + điều hướng theo role
│   ├── common/                # BaseHomeActivity, PlaceholderFragment
│   ├── admin/                 # Quản lý Sinh viên, Môn học
│   └── student/               # Lịch học, Đặt lịch, Calendar
└── util/                      # PasswordHasher, NotificationHelper
```

## Phân công

Xem [docs/PLAN.md](docs/PLAN.md).
