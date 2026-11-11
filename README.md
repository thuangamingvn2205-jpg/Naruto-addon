# Naruto Mod — NeoForge 1.21.1

## Lấy file .jar (không cần cài gì trên máy)
1. Tạo repo GitHub (miễn phí), upload toàn bộ thư mục này (giữ nguyên `.github/`).
2. Vào tab **Actions** -> workflow **Build mod jar** -> chờ ~3-5 phút.
3. Mở lần chạy xong -> mục **Artifacts** -> tải `narutomod-jar` -> giải nén ra `narutomod-1.0.0.jar`.

## Build trên máy
- JDK 21 + Gradle 8.10+ -> `gradle build` -> `build/libs/narutomod-1.0.0.jar`
- Thử trực tiếp: `gradle runClient`
- Nếu Gradle báo không tìm thấy plugin `net.neoforged.moddev` hoặc NeoForge `21.1.172`,
  chỉnh số phiên bản trong `build.gradle` / `gradle.properties` theo bản mới nhất ở https://projects.neoforged.net/neoforged/neoforge

## Cài vào game
NeoForge 1.21.1 (21.1.x) -> bỏ .jar vào thư mục `mods`.

## Chơi
- Lần đầu vào thế giới: nhận Sổ Nhiệm Vụ + 16 Shuriken.
- Sổ Nhiệm Vụ: chuột phải = xem; Shift + chuột phải = nhận / nộp.
- 35 nhiệm vụ: Học viện -> Genin -> Chunin -> Jonin -> Jonin tinh nhuệ -> Kage (`data/narutomod/quests/story.json`).
- Lệnh: /naruto status | quest | quest abandon ; admin (op): /naruto admin rank|xp|unlock mokuton|complete|chakra|reset
- Mộc Độn mở khóa ở nhiệm vụ "Đội Yamato".
