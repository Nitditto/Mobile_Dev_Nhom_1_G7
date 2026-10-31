# Quy tắc dự án

## Git commit

- Mỗi dòng commit message không quá 72 ký tự.
- Commit message tối đa 2 dòng.
- Không thêm dòng `Co-Authored-By` hay bất kỳ trailer co-author nào.

## Quy trình làm việc

- Mỗi bước làm trên branch riêng tạo từ `main` mới nhất
  (`feature/<ten-buoc>`, bước cuối dùng `release/v1.0`).
- Không commit thẳng lên `main`. Xong bước: build xanh, push branch,
  rồi merge vào `main` (qua Pull Request) theo `docs/PLAN.md`.
- Được dùng emulator: chạy app để test thủ công, được viết và chạy
  test `androidTest` / `connectedAndroidTest`, ngoài unit test JVM
  (`app/src/test`).
