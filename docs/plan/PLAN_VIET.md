# Plan của Việt — Bước 2, 5, 8

Việt làm **giữa mỗi vòng**. Thứ tự: Trường → **Việt** → Dũng → Trường → **Việt** → …
Quy trình git & quy ước chung: xem [PLAN.md](../PLAN.md).

| Bước | Việc | Bắt đầu khi | Bàn giao cho |
|---|---|---|---|
| 2 | Quản lý sinh viên | Trường merge Bước 1 | Dũng (Bước 3) |
| 5 | Quản lý trạng thái lịch | Trường merge Bước 4 | Dũng (Bước 6) |
| 8 | Thông báo thay đổi lịch + kiểm thử | Trường merge Bước 7 | Dũng (Bước 9) |

---

## Bước 2 — Quản lý sinh viên (Admin)

Branch: `feature/student-management`
Nhận từ Trường: `fragment_list.xml`, `AppViewModelFactory`, `confirmDelete`, mẫu code Subject (`ui/admin/subject/`).

### File cần tạo

| File | Nội dung |
|---|---|
| `data/repository/StudentRepository.kt` | `create(code, name, email, phone, username, password)`: dùng `database.withTransaction { }` để insert **Account (role STUDENT, password qua `PasswordHasher`)**, rồi insert **Student** với `accountId` vừa tạo. `update(student, newPassword?)`. `delete(student)` = `accountDao.deleteById(student.accountId)` (cascade sẽ xóa Student và lịch học của SV) |
| `ui/admin/student/StudentListFragment.kt` | Copy mẫu `SubjectListFragment`, dùng `studentDao.search()` |
| `ui/admin/student/StudentViewModel.kt` | `query` → `flatMapLatest`, gọi repository |
| `ui/admin/student/StudentAdapter.kt` | Hiện mã SV, họ tên, email, SĐT |
| `ui/admin/student/StudentFormDialog.kt` | Thêm/sửa; khi sửa, password để trống = giữ nguyên |
| `res/layout/item_student.xml`, `dialog_student.xml` | Item + form (mã SV, họ tên, email, SĐT, username, password) |

> Cần thêm vào `AccountDao`: `getById(id)` để lấy username khi mở form sửa.

### Validate

- Email: `Patterns.EMAIL_ADDRESS`
- SĐT: `^0\d{9}$`
- Mã SV không trùng (`studentDao.countByCode`), username không trùng (`accountDao.countByUsername`)

### Commit batch

1. `feat(student): StudentRepository with transactional account + student create`
2. `feat(student): list and search students`
3. `feat(student): add/edit dialog with validation (email, phone, unique code & username)`
4. `feat(student): delete student and wire Students tab`

### Xong khi

- [ ] Admin tạo SV mới → đăng xuất → đăng nhập bằng tài khoản SV vừa tạo được
- [ ] Xóa SV thì tài khoản cũng mất (không đăng nhập được nữa)

### Bàn giao cho Dũng

Admin tạo được tài khoản SV để test; mẫu Repository pattern để Trường/Dũng dùng tiếp.

---

## Bước 5 — Quản lý trạng thái lịch (Pending / Confirmed / Cancelled)

Branch: `feature/schedule-status`
Nhận từ Trường: `ScheduleRepository` + `BookingResult` (mọi thay đổi lịch đi qua đây), `BookingActivity`.

### File cần tạo / sửa

| File | Nội dung |
|---|---|
| `util/StatusUi.kt` | Thêm màu cho từng trạng thái (Pending = cam, Confirmed = xanh lá, Cancelled = xám) dùng trong chip |
| `res/layout/item_schedule.xml` | Đổi text trạng thái thành `Chip` có màu |
| `res/layout/fragment_schedule_list.xml` (hoặc thêm vào layout hiện tại) | `ChipGroup` lọc: Tất cả / Pending / Confirmed / Cancelled |
| `ScheduleViewModel.kt` | Thêm `statusFilter: MutableStateFlow<String?>`, `combine(query, statusFilter)` để lọc |
| `ScheduleAdapter.kt` | Nút ⋮ / long-click mở `PopupMenu`: Xác nhận, Hủy, Chuyển về Pending |
| `ScheduleRepository.kt` | `changeStatus(id, newStatus): BookingResult`. Khi chuyển từ **Cancelled → Pending/Confirmed** phải chạy lại `countConflicts` |

### Commit batch

1. `feat(status): colored status chip in schedule item`
2. `feat(status): filter schedules by status`
3. `feat(status): confirm/cancel/restore actions with conflict re-check`
4. `refactor(schedule): single onScheduleChanged(event) hook in ScheduleRepository`

> Commit 4: tạo `sealed class ScheduleEvent { Created; Updated; StatusChanged; Deleted }` và gọi `onScheduleChanged(event, item)` ở cuối mọi hàm ghi của `ScheduleRepository`. Tạm thời hàm này để trống; Bước 8 sẽ nối thông báo vào.

### Xong khi

- [ ] Lọc theo từng trạng thái đúng
- [ ] Hủy 1 lịch rồi đặt lịch khác trùng giờ: được. Khôi phục lịch đã hủy: báo trùng.

### Bàn giao cho Dũng

Lịch có đủ trạng thái và màu (`StatusUi`) để Calendar dùng lại.

---

## Bước 8 — Thông báo thay đổi lịch + kiểm thử

Branch: `feature/change-notifications`
Nhận từ Trường: `ReminderScheduler.schedule(item)` / `cancel(scheduleId)`.

### File cần tạo / sửa

| File | Nội dung |
|---|---|
| `ScheduleRepository.onScheduleChanged` | Created/Updated → `ReminderScheduler.schedule` + thông báo "Đã đặt/cập nhật lịch"; StatusChanged → Cancelled thì `cancel`, còn lại thì `schedule`; Deleted → `cancel` + thông báo |
| `ui/admin/subject/SubjectViewModel.kt` | Trước khi xóa môn: lấy các lịch của môn đó để `cancel` nhắc (cascade xóa lịch trong DB nhưng không xóa alarm) |
| `app/src/androidTest/.../StudyScheduleDaoTest.kt` | DB `inMemoryDatabaseBuilder`. Test `countConflicts`: chồng giờ = 1; chạm mép = 0; lịch Cancelled bị bỏ qua; `excludeId` loại chính nó; khác ngày = 0. Test `search` theo tên môn / địa điểm |
| `app/src/test/.../DateTimeUtilsTest.kt` | `isEndAfterStart`, `weekRange`, `monthRange` (tháng 2 năm nhuận) |

> Cần thêm vào `StudyScheduleDao`: `getIdsBySubject(subjectId): List<Long>`.

### Commit batch

1. `feat(notify): notify student on schedule create/update/cancel/delete`
2. `feat(notify): keep reminders in sync with schedule changes`
3. `test: Room DAO tests for conflict validation and search`
4. `test: unit tests for DateTimeUtils`

### Xong khi

- [ ] `./gradlew test` xanh; `./gradlew connectedAndroidTest` xanh (cần emulator)
- [ ] Hủy lịch → không còn nhận nhắc của lịch đó

### Bàn giao cho Dũng

App đủ tính năng + có test. Dũng test toàn bộ và phát hành.
