# Plan của Dũng — Bước 3, 6, 9

Dũng **khép lại mỗi vòng**. Thứ tự: Trường → Việt → **Dũng** → Trường → Việt → **Dũng** → …
Quy trình git & quy ước chung: xem [PLAN.md](../PLAN.md).

| Bước | Việc | Bắt đầu khi | Bàn giao cho |
|---|---|---|---|
| 3 | Danh sách lịch học cá nhân | Việt merge Bước 2 | Trường (Bước 4) |
| 6 | Calendar ngày / tuần / tháng | Việt merge Bước 5 | Trường (Bước 7) |
| 9 | Sửa lỗi, hoàn thiện, phát hành v1.0 | Việt merge Bước 8 | Cả nhóm |

---

## Bước 3 — Danh sách lịch học cá nhân (Student)

Branch: `feature/schedule-list`
Nhận từ Trường/Việt: `fragment_list.xml`, `AppViewModelFactory`, `confirmDelete`, `DateTimeUtils`, mẫu code Subject/Student.

### File cần tạo

| File | Nội dung |
|---|---|
| `ui/student/schedule/ScheduleListFragment.kt` | Dùng `fragment_list.xml`; lấy `session.studentId` |
| `ui/student/schedule/ScheduleViewModel.kt` | `query` → `flatMapLatest { studyScheduleDao.search(studentId, it) }`; `delete(item)` |
| `ui/student/schedule/ScheduleAdapter.kt` | `ListAdapter<ScheduleItem>` |
| `res/layout/item_schedule.xml` | Tên môn (mã môn), ngày (hiển thị `dd/MM/yyyy`), giờ `start – end`, địa điểm, trạng thái |
| `util/StatusUi.kt` | `label(status)`: PENDING → "Chờ xác nhận", CONFIRMED → "Đã xác nhận", CANCELLED → "Đã hủy" (Việt sẽ thêm màu) |

> Xóa dùng `studyScheduleDao.getById(item.id)` → `delete(...)`, hoặc thêm `deleteById(id)` vào DAO.

### Commit batch

1. `feat(schedule): ScheduleViewModel, adapter and item layout`
2. `feat(schedule): list and search own schedules, wire Schedules tab`
3. `feat(schedule): delete schedule with confirmation`
4. `feat(schedule): expose onAddClick / onItemClick hooks for booking screen`

> Commit 4: FAB và click item gọi 2 hàm `onAddClick()` / `onItemClick(item)` trong Fragment, tạm thời chỉ `Toast` + `// TODO(Trường): open BookingActivity`.

### Xong khi

- [ ] Đăng nhập `sv01` thấy danh sách lịch (rỗng thì hiện text trống)
- [ ] Tìm theo tên môn / địa điểm đúng; SV khác không thấy lịch của nhau
- [ ] Xóa có hỏi xác nhận

### Bàn giao cho Trường

Callback `onAddClick` / `onItemClick` sẵn chỗ để mở `BookingActivity`.

> Để test có dữ liệu trước khi có màn đặt lịch: tạm insert vài lịch qua Database Inspector của Android Studio, **không commit code seed tạm**.

---

## Bước 6 — Calendar view (ngày / tuần / tháng)

Branch: `feature/calendar`
Nhận từ Việt: lịch có đủ trạng thái, `StatusUi` có màu.

### File cần tạo

| File | Nội dung |
|---|---|
| `ui/student/calendar/CalendarViewModel.kt` | State: `mode` (DAY/WEEK/MONTH) + `selectedDate`. Tính khoảng ngày bằng `DateTimeUtils.weekRange/monthRange` → `studyScheduleDao.observeBetween(studentId, from, to)` |
| `ui/student/calendar/CalendarFragment.kt` | `MaterialButtonToggleGroup` (Ngày/Tuần/Tháng), nút ◀ / Hôm nay / ▶, tiêu đề khoảng ngày |
| `ui/student/calendar/DayGroupAdapter.kt` | Day/Week: list nhóm theo ngày (header ngày + các buổi học, dùng lại `item_schedule.xml`) |
| `ui/student/calendar/MonthGridAdapter.kt` | Month: `GridLayoutManager(7)`, mỗi ô = số ngày + số buổi học (chấm màu); ô ngoài tháng làm mờ |
| `res/layout/fragment_calendar.xml`, `item_calendar_day.xml`, `item_day_header.xml` | |

### Commit batch

1. `feat(calendar): CalendarViewModel with day/week/month ranges`
2. `feat(calendar): day and week views`
3. `feat(calendar): month grid with session count per day`
4. `feat(calendar): prev/next/today navigation, tap day opens day view, wire Calendar tab`

### Xong khi

- [ ] Chuyển 3 chế độ đúng khoảng ngày; tuần bắt đầu từ Thứ 2
- [ ] Bấm 1 ngày trong lưới tháng → chuyển sang chế độ Ngày của ngày đó
- [ ] Đặt/hủy lịch ở tab Lịch học → Calendar tự cập nhật (Flow)

### Bàn giao cho Trường

Đủ màn hình chính, chỉ còn phần thông báo.

---

## Bước 9 — Sửa lỗi, hoàn thiện, phát hành v1.0

Branch: `release/v1.0`
Nhận từ Việt: app đủ tính năng + test.

### Việc cần làm

1. **Test thủ công toàn bộ** theo checklist trong [PLAN.md](../PLAN.md): cả 2 role, xoay màn hình, dark mode, Android 13+ (quyền thông báo). Ghi bug vào GitHub Issues rồi sửa.
2. **UI polish**: empty state cho mọi danh sách, khoảng cách đồng đều, kiểm tra chuỗi tiếng Việt, bỏ `PlaceholderFragment` nếu không còn dùng.
3. **README**: tính năng, tài khoản demo, ảnh chụp màn hình (đặt trong `docs/screenshots/`), phân công 3 thành viên.
4. **Phát hành**: `versionName = "1.0"`, `./gradlew assembleDebug`, tạo tag `v1.0` + GitHub Release đính kèm APK.

### Commit batch

1. `fix: issues found in full manual test pass`
2. `style: UI polish (empty states, dark mode, spacing)`
3. `docs: README with features, demo accounts and screenshots`
4. `chore: release v1.0`

### Xong khi

- [ ] Mọi mục checklist nghiệm thu đã tick
- [ ] `./gradlew assembleDebug test` xanh
- [ ] Release `v1.0` có APK trên GitHub
