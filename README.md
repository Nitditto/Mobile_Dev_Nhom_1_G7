# Mobile_Dev_Nhom_1_G7 — Ứng dụng quản lý & đặt lịch học sinh viên

Ứng dụng Android giúp **quản trị viên** quản lý sinh viên, môn học và giúp **sinh viên**
đặt, quản lý, theo dõi lịch học cá nhân kèm nhắc giờ học.

Android native: **Kotlin · XML Views + ViewBinding · Room · Material 3 · Coroutines/Flow**.
minSdk 24, targetSdk 36. Phiên bản: **1.0**.

## Tính năng

### Chung
- **Đăng nhập 2 vai trò** (Admin / Sinh viên), mỗi vai trò có màn hình chính riêng.
- **Ghi nhớ phiên đăng nhập** bằng SharedPreferences: mở lại app không cần đăng nhập lại.
- **Lưu trữ bằng Room** (SQLite) với 4 bảng `Account`, `Student`, `Subject`, `StudySchedule`;
  mật khẩu được băm SHA-256.
- Hỗ trợ **giao diện sáng / tối** (Material 3 DayNight).

### Quản trị viên
- **Quản lý sinh viên**: xem, tìm kiếm (mã, tên, email, SĐT), thêm, sửa, xóa.
  - Thêm sinh viên tạo luôn tài khoản đăng nhập (1 transaction).
  - Kiểm tra email, SĐT (`0` + 9 số), mã SV và tên đăng nhập không trùng.
  - Sửa: để trống mật khẩu = giữ mật khẩu cũ. Xóa sinh viên xóa luôn tài khoản và lịch học.
- **Quản lý môn học**: xem, tìm kiếm (mã, tên, mô tả), thêm, sửa, xóa.
  - Mã môn không trùng, số tín chỉ từ 1 đến 10. Xóa môn xóa luôn các lịch học của môn.

### Sinh viên
- **Lịch học cá nhân**: xem, tìm kiếm (môn học, địa điểm), lọc theo trạng thái, sửa, xóa.
  Mỗi sinh viên chỉ thấy lịch của mình.
- **Đặt lịch học**: chọn môn, ngày (`MaterialDatePicker`), giờ bắt đầu / kết thúc
  (`MaterialTimePicker`, 24h), địa điểm.
  - Giờ kết thúc phải sau giờ bắt đầu; không đặt ngày / giờ đã qua.
- **Chặn trùng lịch**: hai buổi cùng ngày có khoảng giờ giao nhau bị từ chối và app cho biết
  buổi bị trùng. Hai buổi nối tiếp (08:00–10:00 và 10:00–12:00) vẫn hợp lệ; buổi đã hủy
  không chặn lịch khác.
- **Trạng thái lịch**: *Chờ xác nhận* (mặc định khi đặt), *Đã xác nhận*, *Đã hủy*, hiển thị
  bằng chip màu. Đổi trạng thái qua nút ⋮ hoặc nhấn giữ; khôi phục lịch đã hủy sẽ kiểm tra
  trùng lại.
- **Calendar** ngày / tuần / tháng:
  - Tuần bắt đầu từ Thứ 2; nút ◀ / Hôm nay / ▶ để chuyển kỳ.
  - Lưới tháng hiển thị số buổi học mỗi ngày (không tính buổi đã hủy); bấm một ngày để xem
    chi tiết ngày đó; bấm một buổi học để sửa.
  - Tự cập nhật khi lịch thay đổi.
- **Thông báo**:
  - Nhắc **30 phút trước giờ học** (đặt sát giờ hơn thì nhắc ngay).
  - Thông báo khi lịch được **đặt, cập nhật, đổi trạng thái hoặc xóa**.
  - Nhắc lịch được đặt lại sau khi đăng nhập và sau khi khởi động lại máy; hủy khi đăng xuất.
  - Android 13+ hỏi quyền thông báo; từ chối thì app vẫn chạy bình thường.

## Chạy project

Yêu cầu: Android Studio (bản mới), **JDK 17 hoặc 21** cho Gradle, Android SDK 36.

1. Mở thư mục này bằng Android Studio, chờ Gradle sync.
   Nếu Gradle báo lỗi phiên bản Java, chọn *Settings → Build Tools → Gradle → Gradle JDK* = JDK 21.
2. Chạy cấu hình `app` trên emulator hoặc máy thật (Android 7.0+).

Dòng lệnh:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS; trỏ tới JDK 17/21
./gradlew assembleDebug        # APK: app/build/outputs/apk/debug/app-debug.apk
./gradlew test                 # unit test JVM
./gradlew connectedAndroidTest # test trên emulator / máy thật (nếu có)
```

### Tài khoản demo

Được tạo sẵn khi app chạy lần đầu, cùng 3 môn học mẫu (MOB101, DB101, ENG101).

| Vai trò | Tên đăng nhập | Mật khẩu |
|---|---|---|
| Admin | `admin` | `admin123` |
| Sinh viên | `sv01` | `123456` |

## Cấu trúc

```
app/src/main/java/com/nhom1/studyschedule/
├── StudyScheduleApp.kt          # Application: giữ database + session, tạo kênh thông báo
├── data/
│   ├── AppDatabase.kt           # Room DB + dữ liệu mẫu
│   ├── SessionManager.kt        # Phiên đăng nhập (SharedPreferences)
│   ├── entity/                  # Account, Student, Subject, StudySchedule, ScheduleItem
│   ├── dao/                     # CRUD, tìm kiếm, kiểm tra trùng, truy vấn theo khoảng ngày
│   └── repository/              # StudentRepository, ScheduleRepository (validate + hook thay đổi)
├── notification/                # ReminderScheduler/Receiver, BootReceiver, ChangeNotifier
├── ui/
│   ├── login/                   # Đăng nhập + điều hướng theo vai trò
│   ├── common/                  # BaseHomeActivity, ViewModel factory, dialog dùng chung
│   ├── admin/                   # student/, subject/
│   └── student/                 # schedule/, booking/, calendar/
└── util/                        # DateTimeUtils, StatusUi, PasswordHasher, NotificationHelper
```

Quy ước chính:
- Mỗi màn hình gồm `Fragment` + `ViewModel` + `ListAdapter`; dữ liệu đi từ Room `Flow` → `StateFlow`.
- Ngày lưu dạng `yyyy-MM-dd`, giờ `HH:mm` (chuỗi), chỉ parse / format qua `DateTimeUtils`.
- Mọi thao tác ghi lịch học đi qua `ScheduleRepository`, nơi kiểm tra hợp lệ và cập nhật nhắc lịch / thông báo.
