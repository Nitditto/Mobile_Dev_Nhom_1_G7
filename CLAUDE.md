# Quy tắc dự án

## Git commit

- Mỗi dòng commit message không quá 72 ký tự.
- Commit message tối đa 2 dòng.
- Không thêm dòng `Co-Authored-By` hay bất kỳ trailer co-author nào.

## Quy trình làm việc

- Không tạo branch: commit và push thẳng lên `main`
  (thay cho quy trình branch + PR trong `docs/PLAN.md`).
- Bỏ qua test cần emulator (`androidTest`, `connectedAndroidTest`):
  không viết, không chạy. Chỉ dùng unit test JVM (`app/src/test`).
