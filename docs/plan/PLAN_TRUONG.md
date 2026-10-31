# Plan của Trường — Bước 0, 1, 4, 7

Trường là người **mở đầu mỗi vòng**. Thứ tự: **Trường** → Việt → Dũng → **Trường** → …
Quy trình git & quy ước chung: xem [PLAN.md](../PLAN.md).

| Bước | Việc | Bắt đầu khi | Bàn giao cho |
|---|---|---|---|
| 0 | Push khung project | Ngay | Chính mình (Bước 1) |
| 1 | Tiện ích dùng chung + Quản lý môn học | Bước 0 xong | Việt (Bước 2) |
| 4 | Đặt lịch + kiểm tra trùng lịch | Dũng merge Bước 3 | Việt (Bước 5) |
| 7 | Nhắc lịch học sắp tới | Dũng merge Bước 6 | Việt (Bước 8) |

---

## Bước 0 — Push khung project

Khung đã có sẵn trong máy, build xanh. Chỉ cần commit & push lên `main`:

```bash
git add .gitignore README.md build.gradle.kts settings.gradle.kts gradle.properties gradle gradlew gradlew.bat app docs
git commit -m "chore: init Android project skeleton (Room, session, login, role homes)"
git push origin main
```

> Không commit `local.properties` (đã nằm trong `.gitignore`).

---

## Bước 1 — Tiện ích dùng chung + Quản lý môn học (Admin)

Branch: `feature/subject-management`

### File cần tạo

| File | Nội dung |
|---|---|
| `util/DateTimeUtils.kt` | `parseDate/formatDate`, `parseTime/formatTime`, `today()`, `isEndAfterStart(start, end)`, `weekRange(date)`, `monthRange(date)` (trả về `Pair<String, String>`) |
| `ui/common/AppViewModelFactory.kt` | Factory tạo ViewModel, truyền `StudyScheduleApp` vào constructor |
| `ui/common/Dialogs.kt` | `Fragment.confirmDelete(message, onOk)` dùng `MaterialAlertDialogBuilder` |
| `res/layout/fragment_list.xml` | Layout **dùng chung cho cả nhóm**: ô tìm kiếm (`TextInputLayout`) + `RecyclerView` + `TextView` trống + `FloatingActionButton` |
| `ui/admin/subject/SubjectListFragment.kt` | Dùng `fragment_list.xml`, tìm kiếm theo text gõ vào |
| `ui/admin/subject/SubjectViewModel.kt` | `query: MutableStateFlow<String>` → `flatMapLatest { subjectDao.search(it) }`; insert/update/delete |
| `ui/admin/subject/SubjectAdapter.kt` | `ListAdapter<Subject>`, click = sửa, nút/long-click = xóa |
| `ui/admin/subject/SubjectFormDialog.kt` | Dialog thêm/sửa |
| `res/layout/item_subject.xml`, `dialog_subject.xml` | Item + form (mã, tên, mô tả, số tín chỉ) |

### Commit batch

1. `feat(common): add DateTimeUtils, ViewModel factory, confirm dialog, shared list layout`
2. `feat(subject): list and search subjects`
3. `feat(subject): add/edit subject dialog with validation (unique code, credits > 0)`
4. `feat(subject): delete with confirmation and wire Subjects tab in AdminHomeActivity`

### Xong khi

- [ ] Admin xem / tìm / thêm / sửa / xóa được môn học
- [ ] Mã môn trùng → báo lỗi, không crash
- [ ] `AdminHomeActivity` tab Môn học dùng `SubjectListFragment`

### Bàn giao cho Việt

`fragment_list.xml`, `AppViewModelFactory`, `confirmDelete`, `DateTimeUtils`, và bộ Fragment/ViewModel/Adapter của Subject làm mẫu để copy.

---

## Bước 4 — Đặt lịch học + kiểm tra trùng lịch (Student)

Branch: `feature/booking`
Nhận từ Dũng: `ScheduleListFragment` đã có callback `onAddClick` / `onItemClick` (đang `TODO`).

### File cần tạo

| File | Nội dung |
|---|---|
| `data/repository/ScheduleRepository.kt` | `book(schedule)` / `update(schedule)` trả về `BookingResult` |
| `data/repository/BookingResult.kt` | `sealed class BookingResult { Success; Conflict; Invalid(@StringRes reason) }` |
| `ui/student/booking/BookingActivity.kt` | Form đặt lịch; chế độ sửa qua `EXTRA_SCHEDULE_ID` |
| `ui/student/booking/BookingViewModel.kt` | Load danh sách môn, giữ state form, gọi repository |
| `res/layout/activity_booking.xml` | Chọn môn (Exposed Dropdown), ngày (`MaterialDatePicker`), giờ bắt đầu/kết thúc (`MaterialTimePicker`), địa điểm, nút Lưu |

### Quy tắc validate trong `ScheduleRepository`

1. Đủ trường: môn, ngày, giờ, địa điểm
2. `DateTimeUtils.isEndAfterStart(start, end)`
3. Ngày không ở quá khứ (`studyDate >= today()`)
4. `studyScheduleDao.countConflicts(studentId, date, start, end, excludeId) == 0`. Khi sửa thì truyền `excludeId = schedule.id`, khi tạo mới truyền `0`.
5. Lịch mới có status `PENDING`

### Commit batch

1. `feat(booking): ScheduleRepository with conflict validation`
2. `feat(booking): booking form with subject dropdown, date and time pickers`
3. `feat(booking): save booking (status PENDING) and show conflict/invalid errors`
4. `feat(booking): edit mode and open from schedule list FAB/item`

### Xong khi

- [ ] Đặt lịch mới hiện ngay trong danh sách
- [ ] Đặt 2 lịch chồng giờ cùng ngày → báo "Trùng lịch"; lịch nối tiếp nhau (08:00–10:00 và 10:00–12:00) vẫn hợp lệ
- [ ] Sửa lịch không tự báo trùng với chính nó

### Bàn giao cho Việt

**Mọi thay đổi lịch phải đi qua `ScheduleRepository`** (Việt sẽ thêm đổi trạng thái + hook thông báo vào đây).

---

## Bước 7 — Nhắc lịch học sắp tới

Branch: `feature/reminders`
Nhận từ Dũng: app đủ màn hình (danh sách, đặt lịch, trạng thái, calendar).

### File cần tạo / sửa

| File | Nội dung |
|---|---|
| `notification/ReminderScheduler.kt` | `schedule(item: ScheduleItem)`: đặt alarm trước giờ học 30 phút bằng `AlarmManager.setAndAllowWhileIdle` (không cần quyền exact alarm); `cancel(scheduleId)`; `rescheduleAll(studentId)` dùng `getUpcoming()` |
| `notification/ReminderReceiver.kt` | Nhận alarm → `NotificationHelper.show(...)` "Sắp đến giờ học: <môn> lúc <giờ> tại <địa điểm>" |
| `notification/BootReceiver.kt` | `BOOT_COMPLETED` → nếu đang đăng nhập Student thì `rescheduleAll` |
| `StudentHomeActivity.kt` | Xin quyền `POST_NOTIFICATIONS` (Android 13+) bằng `registerForActivityResult` |
| `LoginActivity.kt` | Sau khi Student đăng nhập → `rescheduleAll` |
| `AndroidManifest.xml` | Đăng ký 2 receiver + quyền `RECEIVE_BOOT_COMPLETED` |

> Dùng `scheduleId` làm `requestCode` của `PendingIntent` để hủy đúng alarm.

### Commit batch

1. `feat(notify): ReminderScheduler and ReminderReceiver`
2. `feat(notify): request notification permission on student home`
3. `feat(notify): reschedule reminders after login and device boot`
4. `chore(notify): register receivers and permissions in manifest`

### Xong khi

- [ ] Đặt lịch bắt đầu sau ~31 phút → khoảng 1 phút sau có thông báo nhắc
- [ ] Từ chối quyền thông báo → app không crash

### Bàn giao cho Việt

API `ReminderScheduler.schedule(item)` / `cancel(scheduleId)` sẵn sàng để nối vào hook `onScheduleChanged`.
