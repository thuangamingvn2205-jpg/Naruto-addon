import json, os
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src/main/resources/data/narutomod/quests/story.json")

def K(count, text, name="", target=""): return {"type":"kill","target":target,"name":name,"count":count,"text":text}
def C(item, count, text): return {"type":"collect","target":item,"count":count,"text":text}
def U(key, count, text): return {"type":"use","target":key,"count":count,"text":text}
def T(blocks, text): return {"type":"travel","count":blocks,"text":text}
def S(entity, name, hp, count=1, dist=16): return {"entity":entity,"name":name,"health":hp,"count":count,"distance":dist}
def R(xp=0, ryo=0, items=None, promote="", unlock=None):
    d={"xp":xp,"ryo":ryo}
    if items: d["items"]=[{"id":"narutomod:"+i if ":" not in i else i,"count":c} for i,c in items]
    if promote: d["promote"]=promote
    if unlock: d["unlock"]=unlock
    return d
Q=[]
def q(id,arc,rank,title,story,obj,spawns,rew): Q.append({"id":id,"arc":arc,"rank":rank,"title":title,"story":story,"objectives":obj,"spawns":spawns,"rewards":rew})

A="Học viện"
q("academy_01",A,"academy","Ngày tốt nghiệp Học viện",
  "Iruka-sensei triệu tập cả lớp: để nhận băng đô Làng Lá, mỗi học viên phải qua bài thi thực hành. Hãy chứng tỏ tay nghề ném vũ khí của em.",
  [U("shuriken",10,"Ném phi tiêu Shuriken luyện tập")],[],R(80,100,[("shuriken",8)]))
q("academy_02",A,"academy","Mizuki và Cuộn Cấm Thuật",
  "Mizuki dụ Naruto đánh cắp Cuộn Cấm Thuật khỏi tòa nhà Hokage, rồi lộ mặt phản bội. Hãy ngăn hắn lại — và nhận lấy băng đô của mình.",
  [K(1,"Hạ gục Mizuki","Mizuki")],[S("minecraft:vindicator","Mizuki",40)],
  R(200,300,[("konoha_headband",1)],promote="genin"))

G="Đội 7"
q("genin_01",G,"genin","Bài kiểm tra chuông của Kakashi",
  "Kakashi Hatake đặt ra bài thi sinh tử: hai cái chuông cho ba học trò. Chỉ khi phối hợp đồng đội, bạn mới vượt qua.",
  [K(1,"Hạ gục Kakashi (huấn luyện)","Kakashi")],[S("minecraft:vindicator","Kakashi Hatake (Huấn luyện)",60)],R(250,300))
q("genin_02",G,"genin","Nhiệm vụ hạng D: Bắt mèo Tora",
  "Con mèo Tora của vợ Daimyo lại bỏ trốn. Đội 7 phải lùng sục khắp làng — nhiệm vụ nhỏ nhưng khiến Naruto phát cáu.",
  [T(150,"Truy lùng khắp làng (di chuyển 150 block)"),C("minecraft:cod",2,"Chuẩn bị 2 con cá làm mồi nhử")],[],
  R(200,400,[("ramen",2),("fireball_scroll",1)]))
q("genin_03",G,"genin","Hạng C: Hộ tống Tazuna",
  "Nhiệm vụ hộ tống tưởng chừng đơn giản bỗng thành hạng A khi Quỷ Huynh Đệ (Gozu & Meizu) phục kích giữa đường.",
  [T(250,"Hộ tống Tazuna về Xứ Sóng (250 block)"),K(2,"Hạ gục Quỷ Huynh Đệ","Quỷ Huynh Đệ")],
  [S("minecraft:vindicator","Quỷ Huynh Đệ Gozu",45),S("minecraft:vindicator","Quỷ Huynh Đệ Meizu",45)],R(350,600))
q("genin_04",G,"genin","Quỷ nhân Zabuza",
  "Zabuza Momochi, Quỷ Nhân Làng Sương Mù, chặn đường với thanh Kubikiribōchō. Trận chiến trong màn sương bắt đầu.",
  [K(1,"Hạ gục Zabuza Momochi","Zabuza")],[S("minecraft:vindicator","Zabuza Momochi",130,1,18)],R(500,800,[("throwing_kunai",8)]))
q("genin_05",G,"genin","Haku và Băng Độn",
  "Haku — cậu bé mang Kekkei Genkai Băng Độn — bảo vệ Zabuza bằng những tấm gương băng. Hãy phá vỡ chúng.",
  [K(1,"Hạ gục Haku","Haku")],[S("minecraft:stray","Haku",80)],R(500,800))
q("genin_06",G,"genin","Cây cầu Vĩ Đại",
  "Để giải phóng Xứ Sóng khỏi Gato, dân làng cùng Đội 7 hoàn tất cây cầu — sau này được gọi là Cầu Naruto.",
  [K(8,"Đánh bại lính đánh thuê của Gato","Gato"),C("minecraft:oak_planks",32,"Gom 32 ván gỗ sồi để xây cầu")],
  [S("minecraft:pillager","Lính đánh thuê của Gato",22,8,18)],R(600,1000,[("ramen",3)]))

E="Kỳ thi Chunin"
q("exam_01",E,"genin","Đơn đăng ký Kỳ thi Chunin",
  "Kakashi đề cử Đội 7 tham gia Kỳ thi Chunin. Hãy chuẩn bị giấy tờ và vũ khí cho kỳ thi khắc nghiệt nhất đời genin.",
  [C("minecraft:paper",3,"Chuẩn bị 3 tờ giấy (đơn đăng ký)"),C("minecraft:iron_ingot",2,"Mang theo 2 thỏi sắt làm vũ khí")],[],R(200,300))
q("exam_02",E,"genin","Phần thi viết của Ibiki",
  "Ibiki Morino ra đề bất khả thi — chỉ ai biết thu thập thông tin mà không bị phát hiện mới qua được.",
  [C("minecraft:book",1,"Mang theo 1 quyển sách (ghi chép)"),C("minecraft:ink_sac",1,"Mang theo 1 túi mực")],[],R(250,300))
q("exam_03",E,"genin","Rừng Tử Thần",
  "Anko đưa thí sinh vào Rừng Tử Thần, nơi thú dữ và ninja nước ngoài rình rập. Rồi một luồng chakra kinh hoàng xuất hiện: Orochimaru.",
  [K(6,"Diệt rắn khổng lồ trong rừng","Rắn khổng lồ","minecraft:spider"),T(150,"Chạy thoát khỏi Orochimaru (150 block)")],
  [S("minecraft:spider","Rắn khổng lồ",24,6,12),S("minecraft:evoker","Orochimaru",220,1,24)],R(700,600))
q("exam_04",E,"genin","Vòng sơ loại: Kiba Inuzuka",
  "Trận đấu đơn đầu tiên. Kiba và Akamaru chiến đấu như một thể thống nhất — bạn phải nhanh hơn họ.",
  [K(1,"Hạ gục Kiba Inuzuka","Kiba")],[S("minecraft:vindicator","Kiba Inuzuka",60)],R(400,500))
q("exam_05",E,"genin","Chung kết: Neji Hyuga",
  "Thiên tài tộc Hyuga tuyên bố số phận không thể thay đổi. Hãy chứng minh điều ngược lại như Naruto đã làm.",
  [K(1,"Hạ gục Neji Hyuga","Neji")],[S("minecraft:vindicator","Neji Hyuga",120)],R(600,800))
q("exam_06",E,"genin","Đại chiến Konoha",
  "Orochimaru và Làng Cát phát động tấn công giữa lễ chung kết. Gaara hóa thành quái thú Shukaku. Bảo vệ ngôi làng!",
  [K(12,"Đánh bại lính Âm Ẩn","Lính Âm Ẩn"),K(1,"Hạ gục Gaara (Shukaku)","Gaara")],
  [S("minecraft:zombie","Lính Âm Ẩn",20,12,20),S("minecraft:ravager","Gaara (Shukaku)",260,1,24)],
  R(1200,2000,[("ramen",5)],promote="chunin"))

TS="Tìm Tsunade"
q("tsunade_01",TS,"chunin","Truy tìm Tsunade",
  "Sau cái chết của Đệ Tam, Jiraiya dẫn Naruto đi tìm Sannin Tsunade để mời cô làm Ngũ Đại Hokage. Trên đường, Jiraiya dạy Naruto Rasengan.",
  [T(400,"Lên đường tìm Tsunade (400 block)"),C("minecraft:gold_ingot",5,"Gom 5 thỏi vàng trả nợ cờ bạc cho Tsunade")],[],
  R(500,500,[("rasengan_scroll",1)]))
q("tsunade_02",TS,"chunin","Rèn luyện Rasengan",
  "Rasengan — kỹ thuật chỉ Đệ Tứ Hokage từng thành thạo. Hãy hoàn thiện nó trước khi quá muộn.",
  [U("rasengan",8,"Tung Rasengan trúng mục tiêu 8 lần")],[],R(600,500))
q("tsunade_03",TS,"chunin","Kabuto & Orochimaru",
  "Orochimaru và Kabuto phục kích khi Tsunade chấp nhận lời mời. Cô vượt qua nỗi sợ máu để chiến đấu, và trở thành Ngũ Đại Hokage.",
  [K(1,"Hạ gục Kabuto Yakushi","Kabuto"),K(1,"Đẩy lùi Orochimaru","Orochimaru")],
  [S("minecraft:evoker","Kabuto Yakushi",100),S("minecraft:evoker","Orochimaru",250)],R(900,1500,[("ramen",3)]))

SA="Truy đuổi Sasuke"
q("sasuke_01",SA,"chunin","Sasuke bỏ làng",
  "Sasuke chọn con đường của sức mạnh, rời Konoha theo Âm Tứ Tấu. Đội Cứu Viện do Shikamaru dẫn đầu lên đường.",
  [T(300,"Truy đuổi Sasuke (300 block)"),K(4,"Hạ gục Âm Tứ Tấu","Âm Tứ Tấu")],
  [S("minecraft:pillager","Âm Tứ Tấu - Jirobo",70),S("minecraft:pillager","Âm Tứ Tấu - Kidomaru",70),
   S("minecraft:pillager","Âm Tứ Tấu - Sakon",70),S("minecraft:pillager","Âm Tứ Tấu - Tayuya",70)],R(1000,1500))
q("sasuke_02",SA,"chunin","Thung lũng Chung Cuộc",
  "Naruto và Sasuke đối đầu bên thác nước, giữa Rasengan và Chidori. Sasuke rời đi, mang theo một lời hứa chưa thành.",
  [K(1,"Hạ gục Sasuke Uchiha (Ấn Nguyền)","Sasuke")],[S("minecraft:vindicator","Sasuke Uchiha (Ấn Nguyền)",260)],R(1500,2500))

SH="Naruto Shippuden"
q("shp_01",SH,"chunin","Hai năm rưỡi rèn luyện",
  "Naruto cùng Jiraiya tu luyện suốt hai năm rưỡi; Sasuke ở bên Orochimaru; Sakura học y thuật từ Tsunade. Ai cũng mạnh hơn.",
  [T(500,"Rèn luyện dã ngoại (500 block)"),U("rasengan",15,"Luyện Rasengan 15 lần"),U("fireball",10,"Luyện Hỏa độn 10 lần")],[],R(1000,1000,[("ramen",3)]))
q("shp_02",SH,"chunin","Giải cứu Kazekage Gaara",
  "Akatsuki bắt Gaara để chiết xuất Nhất Vĩ. Đội Kakashi đuổi theo Deidara và Sasori.",
  [K(1,"Hạ gục Sasori","Sasori"),K(1,"Hạ gục Deidara","Deidara")],
  [S("minecraft:evoker","Sasori",220),S("minecraft:witch","Deidara",180)],R(1800,3000))
q("shp_03",SH,"chunin","Đội Yamato — Hạt giống Mộc Độn",
  "Đội Kakashi có thêm Đội trưởng Yamato, người sống sót duy nhất sau các thí nghiệm tế bào Hashirama của Orochimaru và sở hữu Mộc Độn. Anh dạy bạn những kỹ thuật cơ bản.",
  [C("minecraft:oak_log",32,"Gom 32 khúc gỗ sồi để luyện Mộc Độn"),C("minecraft:oak_sapling",8,"Gom 8 cây non")],[],
  R(900,800,[("wood_clone_scroll",1),("wood_house_scroll",1)],unlock=["mokuton"]))
q("shp_04",SH,"chunin","Luyện tập Mộc Độn",
  "Yamato yêu cầu bạn thành thạo Mộc Phân Thân và dựng nhà gỗ Tứ Trụ Gia để cắm trại giữa rừng như Đội 7.",
  [U("wood_clone",3,"Thi triển Mộc Phân Thân 3 lần"),U("wood_house",2,"Dựng nhà Tứ Trụ Gia 2 lần")],[],R(1000,800))
q("shp_05",SH,"chunin","Hidan & Kakuzu — Cái chết của Asuma",
  "Cặp đôi Akatsuki bất tử Hidan và Kakuzu sát hại Asuma Sarutobi. Shikamaru thề báo thù.",
  [K(1,"Hạ gục Hidan","Hidan"),K(1,"Hạ gục Kakuzu","Kakuzu")],
  [S("minecraft:vindicator","Hidan",280),S("minecraft:ravager","Kakuzu",320)],
  R(2500,4000,[("wood_prison_scroll",1),("wood_spear_scroll",1)],promote="jonin"))

J="Akatsuki"
q("jonin_01",J,"jonin","Cầu Thiên Địa",
  "Yamato dẫn Đội 7 tới Cầu Thiên Địa gặp Sasuke; anh dùng Mộc Độn để kiềm chế Naruto khi Cửu Vĩ trỗi dậy.",
  [T(500,"Tới Cầu Thiên Địa (500 block)"),U("wood_prison",3,"Dùng Tứ Trụ Lao 3 lần"),U("wood_spear",3,"Dùng Đại Thụ Thương 3 lần")],[],
  R(1500,1500,[("wood_dragon_scroll",1)]))
q("jonin_02",J,"jonin","Truy đuổi Itachi",
  "Itachi Uchiha và Kisame — cặp đôi Akatsuki khét tiếng. Sasuke thề giết anh trai, còn bạn phải ngăn bi kịch.",
  [K(1,"Hạ gục Itachi Uchiha","Itachi"),K(1,"Hạ gục Kisame Hoshigaki","Kisame")],
  [S("minecraft:vindicator","Itachi Uchiha",350),S("minecraft:ravager","Kisame Hoshigaki",330)],R(3000,5000))
q("jonin_03",J,"jonin","Pain tấn công Konoha",
  "Nagato điều khiển Lục Đạo Pain, san bằng Konoha bằng Shinra Tensei. Naruto trở về từ núi Myoboku, quyết đối đầu.",
  [K(6,"Hạ gục Lục Đạo Pain","Lục Đạo Pain"),U("wood_dragon",3,"Dùng Mộc Long 3 lần")],
  [S("minecraft:vindicator","Lục Đạo Pain - Thiên Đạo",110,1,18),S("minecraft:vindicator","Lục Đạo Pain - Nhân Gian Đạo",110,1,18),
   S("minecraft:vindicator","Lục Đạo Pain - Súc Sinh Đạo",110,1,18),S("minecraft:vindicator","Lục Đạo Pain - Tu La Đạo",110,1,18),
   S("minecraft:witch","Lục Đạo Pain - Ngạ Quỷ Đạo",110,1,18),S("minecraft:evoker","Lục Đạo Pain - Địa Ngục Đạo",110,1,18)],
  R(3500,6000,[("ramen",5)]))
q("jonin_04",J,"jonin","Hội nghị Ngũ Kage",
  "Năm Kage họp mặt chống Akatsuki. Sasuke xuất hiện tấn công hội nghị, còn Danzo bị cuốn vào âm mưu của chính mình.",
  [T(600,"Tới nơi họp Ngũ Kage (600 block)"),K(1,"Hạ gục Danzo Shimura","Danzo"),K(1,"Đánh bại Sasuke (Mangekyō)","Mangekyō")],
  [S("minecraft:vindicator","Danzo Shimura",300),S("minecraft:vindicator","Sasuke Uchiha (Mangekyō)",300)],
  R(3500,6000,promote="elite_jonin"))

W="Đại chiến Ninja lần 4"
q("war_01",W,"elite_jonin","Tiền tuyến của Liên Minh Ninja",
  "Madara tung đội quân Bạch Zetsu. Liên Minh Ninja đứng lên, chia thành các đội tiền phương.",
  [K(20,"Tiêu diệt Bạch Zetsu","Bạch Zetsu")],[S("minecraft:zombie","Bạch Zetsu",24,20,22)],R(4000,5000,[("ramen",5)]))
q("war_02",W,"elite_jonin","Obito — Kẻ đeo mặt nạ",
  "Tobi hóa ra là Obito Uchiha — người từng là đồng đội của Kakashi, nay muốn khởi động Kế hoạch Nguyệt Nhãn.",
  [K(1,"Hạ gục Obito Uchiha","Obito")],[S("minecraft:vindicator","Obito Uchiha (Tobi)",480)],
  R(5000,7000,[("wood_forest_scroll",1),("wood_golem_scroll",1)]))
q("war_03",W,"elite_jonin","Madara Uchiha — Huyền thoại sống",
  "Madara sống lại bằng Edo Tensei rồi đoạt Rinnegan. Hashirama đối mặt người bạn cũ — và Mộc Độn lại vang lên giữa chiến trường.",
  [K(1,"Hạ gục Madara Uchiha","Madara"),U("wood_golem",3,"Triệu hồi Mộc Nhân 3 lần"),U("wood_forest",2,"Dùng Thụ Hải Giáng Thế 2 lần")],
  [S("minecraft:vindicator","Madara Uchiha",700,1,22)],R(6500,9000))
q("war_04",W,"elite_jonin","Kaguya Ōtsutsuki",
  "Mẹ của mọi chakra thức tỉnh. Naruto và Sasuke phải hợp sức phong ấn bà.",
  [K(1,"Hạ gục Kaguya Ōtsutsuki","Kaguya")],[S("minecraft:ravager","Kaguya Ōtsutsuki",900,1,24)],R(8000,12000))
q("war_05",W,"elite_jonin","Trận chiến cuối ở Thung lũng Chung Cuộc",
  "Hai người bạn, hai lý tưởng đối đầu lần cuối bằng tất cả sức mạnh. Khi cát bụi lắng xuống, họ trở lại làm bạn.",
  [K(1,"Đánh bại Sasuke (Rinnegan)","Rinnegan")],[S("minecraft:vindicator","Sasuke Uchiha (Rinnegan)",600)],R(9000,15000))

H="Con đường Hokage"
q("kage_01",H,"elite_jonin","Bảo vệ ngôi làng",
  "Sau chiến tranh, Hội đồng Hỏa Quốc xét duyệt ứng viên Hokage. Bạn phải chứng minh mình bảo vệ được làng trước mọi hiểm họa.",
  [K(15,"Hạ gục 15 zombie",target="minecraft:zombie"),K(15,"Hạ gục 15 skeleton",target="minecraft:skeleton"),
   K(10,"Hạ gục 10 creeper",target="minecraft:creeper"),K(10,"Hạ gục 10 nhện",target="minecraft:spider")],[],R(5000,10000))
q("kage_02",H,"elite_jonin","Lễ nhậm chức Hokage",
  "Ước mơ từ thuở bé: trở thành Hokage để mọi người công nhận. Hãy dâng lễ vật và nhận lấy chiếc nón Hokage.",
  [C("minecraft:diamond",10,"Dâng 10 kim cương"),C("minecraft:emerald",10,"Dâng 10 ngọc lục bảo"),
   C("minecraft:golden_apple",3,"Dâng 3 táo vàng")],[],R(20000,50000,[("kage_hat",1)],promote="kage"))

json.dump(Q, open(OUT,"w"), ensure_ascii=False, indent=1)
print(len(Q), "quests")
