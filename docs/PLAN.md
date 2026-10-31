# Kế hoạch phân công — Nhóm 1 (Trường · Việt · Dũng)

Plan chi tiết của từng người:

- [Trường](plan/PLAN_TRUONG.md): Bước 0, 1, 4, 7
- [Việt](plan/PLAN_VIET.md): Bước 2, 5, 8
- [Dũng](plan/PLAN_DUNG.md): Bước 3, 6, 9

## Thứ tự tiếp sức

| Vòng | Trường | → Việt | → Dũng |
|---|---|---|---|
| 0. Khung | Bước 0: push khung project | | |
| 1. CRUD | Bước 1: Tiện ích dùng chung + Môn học | Bước 2: Sinh viên | Bước 3: Danh sách lịch học |
| 2. Đặt lịch | Bước 4: Đặt lịch + chặn trùng | Bước 5: Trạng thái lịch | Bước 6: Calendar |
| 3. Hoàn thiện | Bước 7: Nhắc giờ học | Bước 8: Thông báo thay đổi + test | Bước 9: Sửa lỗi + phát hành v1.0 |

Mỗi bước chỉ bắt đầu khi bước trước **đã merge vào `main`**. Xong bước của mình thì báo người kế tiếp trong group chat.

## Quy trình 1 bước

```bash
git checkout main && git pull
git checkout -b feature/<ten-buoc>
# ... code + commit theo batch trong plan cá nhân ...
./gradlew assembleDebug                      # bắt buộc build xanh trước khi push
git push -u origin feature/<ten-buoc>
# Tạo Pull Request -> merge vào main -> báo người tiếp theo
```

## Quy ước chung

- Commit message dạng `type(scope): mô tả` (`feat`, `fix`, `refactor`, `test`, `docs`, `style`, `chore`).
- Package: `data/` (entity, dao, repository), `ui/<role>/<feature>/`, `util/`, `notification/`.
- Mỗi màn hình: `XxxFragment` + `XxxViewModel` + `XxxAdapter` (`ListAdapter`), ViewBinding.
- Ngày `yyyy-MM-dd`, giờ `HH:mm` (String). Parse/format **chỉ qua `DateTimeUtils`**.
- Text UI đặt trong `strings.xml`.
- Truy cập DB/session qua `(application as StudyScheduleApp).database / .session`.
- Tài khoản demo: `admin / admin123`, `sv01 / 123456`.

## Checklist nghiệm thu (đối chiếu đề bài)

- [ ] Login 2 role, lưu phiên bằng SharedPreferences — Bước 0
- [ ] Lưu dữ liệu bằng Room — Bước 0
- [ ] CRUD + tìm kiếm Môn học — Bước 1
- [ ] CRUD + tìm kiếm Sinh viên — Bước 2
- [ ] CRUD + tìm kiếm lịch học cá nhân — Bước 3, 4
- [ ] Đặt lịch (môn, ngày, giờ, địa điểm) — Bước 4
- [ ] Chặn trùng lịch — Bước 4, 5
- [ ] Trạng thái Pending/Confirmed/Cancelled — Bước 5
- [ ] Calendar ngày/tuần/tháng — Bước 6
- [ ] Thông báo sắp đến giờ học & thay đổi lịch — Bước 7, 8
- [ ] Làm việc nhóm qua GitHub (branch, PR, commit theo người) — tất cả
