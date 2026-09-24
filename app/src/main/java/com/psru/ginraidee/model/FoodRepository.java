package com.psru.ginraidee.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ฐานข้อมูลอาหารแบบ offline — เก็บไว้ในเครื่อง ไม่ต้องต่อเน็ต
 *
 * <p>ตอนนี้ hard-code ไว้ในโค้ดก่อนเพื่อให้แอปทำงานได้ครบทันที
 * ถ้าอยากอัปเกรดทีหลัง ให้ย้ายไปอ่านจาก assets/foods.json หรือ Room database
 * โดยแก้แค่คลาสนี้คลาสเดียว ส่วนอื่นของแอปไม่ต้องแก้เลย</p>
 */
public final class FoodRepository {

    private FoodRepository() {
        // utility class ห้ามสร้าง object
    }

    public static List<Food> createDefaultFoods() {
        List<Food> foods = new ArrayList<>();
        foods.addAll(healthyFoods());
        foods.addAll(fastFoods());
        foods.addAll(sharingFoods());
        foods.addAll(dessertFoods());
        return foods;
    }

    // ---------------- 🥗 เพื่อสุขภาพ ----------------
    private static List<Food> healthyFoods() {
        return Arrays.asList(
                new HealthyFood("สลัดอกไก่", 79, 320, "", "อกไก่ย่าง ผักสด น้ำสลัดใส", 88, false),
                new HealthyFood("ข้าวกล้องผัดผัก", 55, 380, "", "ข้าวกล้องหอมมะลิผัดผักรวม", 80, true),
                new HealthyFood("แกงจืดเต้าหู้หมูสับ", 60, 240, "", "น้ำซุปใส เบา ๆ สบายท้อง", 78, false),
                new HealthyFood("ยำวุ้นเส้นกุ้งสด", 70, 290, "", "เปรี้ยวจัดจ้าน แคลอรี่ต่ำ", 75, false),
                new HealthyFood("อกไก่นึ่งซีอิ๊ว", 65, 260, "", "โปรตีนสูง ไขมันต่ำ", 90, false),
                new HealthyFood("ต้มยำน้ำใส", 75, 210, "", "สมุนไพรเต็มถ้วย ไม่ใส่กะทิ", 82, false),
                new HealthyFood("สลัดผักโรลเจ", 59, 230, "", "ผักสดห่อแป้งเวียดนาม จิ้มน้ำจิ้มถั่ว", 85, true),
                new HealthyFood("ข้าวต้มปลา", 65, 300, "", "ย่อยง่าย เหมาะกับมื้อเช้าหรือมื้อดึก", 76, false),
                new HealthyFood("สเต๊กปลาแซลมอนย่าง", 220, 420, "", "โอเมก้า 3 สูง เสิร์ฟกับผักย่าง", 92, false),
                new HealthyFood("เต้าหู้ทรงเครื่อง", 55, 270, "", "โปรตีนจากพืชล้วน", 81, true)
        );
    }

    // ---------------- ⚡ เน้นความเร็ว ----------------
    private static List<Food> fastFoods() {
        return Arrays.asList(
                new FastFood("ข้าวกะเพราหมูสับไข่ดาว", 60, 620, "", "เมนูสามัญประจำชาติ สั่งปุ๊บได้ปั๊บ", 8, true),
                new FastFood("ข้าวมันไก่", 50, 590, "", "ตักปุ๊บเสิร์ฟปั๊บ ไม่ต้องรอ", 5, true),
                new FastFood("ผัดซีอิ๊วหมู", 55, 640, "", "เส้นใหญ่ผัดไฟแรง", 8, true),
                new FastFood("ข้าวหมูทอดกระเทียม", 55, 680, "", "หอมกระเทียมเจียว ทำไว หมด", 7, true),
                new FastFood("บะหมี่กึ่งสำเร็จรูปต้มยำใส่ไข่", 25, 450, "", "ทำเองได้ใน 3 นาที", 3, false),
                new FastFood("แซนด์วิชไข่ทูน่า", 45, 380, "", "หยิบจากตู้แช่ อุ่นแป๊บเดียว", 2, true),
                new FastFood("ข้าวไข่เจียวหมูสับ", 45, 560, "", "ง่าย เร็ว อิ่มแน่นอน", 6, true),
                new FastFood("เบอร์เกอร์ไก่กรอบ", 89, 720, "", "สั่งที่เคาน์เตอร์ รอไม่นาน", 6, true),
                new FastFood("ก๋วยเตี๋ยวหมูน้ำตก", 55, 480, "", "ลวกเส้นเสร็จก็ได้กิน", 6, true),
                new FastFood("ข้าวหน้าไก่เทอริยากิ", 79, 610, "", "ราดซอสเสร็จเสิร์ฟทันที", 7, true)
        );
    }

    // ---------------- 🍲 สายปาร์ตี้-ครอบครัว ----------------
    private static List<Food> sharingFoods() {
        return Arrays.asList(
                new SharingFood("หมูกระทะ", 299, 1200, "", "ปิ้งย่างบุฟเฟ่ต์ นั่งคุยกันยาว ๆ", 4, false),
                new SharingFood("ชาบูหม้อไฟ", 350, 950, "", "ลวกจิ้มกันทั้งโต๊ะ", 4, false),
                new SharingFood("ส้มตำไก่ย่างข้าวเหนียว", 180, 850, "", "ชุดอีสานครบเครื่อง", 3, true),
                new SharingFood("ต้มยำกุ้งหม้อไฟ", 250, 700, "", "เผ็ดร้อน เปรี้ยวนำ กินได้ทั้งบ้าน", 4, true),
                new SharingFood("พิซซ่าถาดใหญ่", 399, 1600, "", "แบ่งกันกินได้หลายชิ้น", 4, false),
                new SharingFood("ปลาทับทิมนึ่งมะนาว", 320, 620, "", "เมนูโต๊ะจีนที่ใครก็ชอบ", 4, true),
                new SharingFood("ไก่ทอดถังใหญ่", 359, 1400, "", "สายปาร์ตี้ห้ามพลาด", 5, false),
                new SharingFood("สุกี้แห้งทะเล", 150, 650, "", "จานใหญ่ แบ่งกัน 2-3 คนกำลังดี", 3, true),
                new SharingFood("ข้าวผัดปูจานใหญ่", 220, 780, "", "สั่งมากลางโต๊ะ ตักแบ่งกัน", 3, false),
                new SharingFood("หม้อไฟเห็ดรวม", 260, 540, "", "เมนูกลางโต๊ะสำหรับสายผัก", 4, false)
        );
    }

    // ---------------- 🍰 ของหวาน-มื้อเบา ๆ ----------------
    private static List<Food> dessertFoods() {
        return Arrays.asList(
                new DessertFood("บิงซูนม", 129, 480, "", "น้ำแข็งใสเกล็ดหิมะ ราดนมข้น", 4, true),
                new DessertFood("ข้าวเหนียวมะม่วง", 89, 520, "", "ของหวานไทยระดับตำนาน", 5, false),
                new DessertFood("บัวลอยไข่หวาน", 45, 330, "", "อุ่น ๆ หวานกำลังดี", 4, false),
                new DessertFood("เค้กช็อกโกแลตลาวา", 119, 450, "", "ตัดแล้วช็อกโกแลตไหลเยิ้ม", 5, false),
                new DessertFood("โยเกิร์ตกรีกผลไม้รวม", 79, 210, "", "หวานน้อย สดชื่น", 2, true),
                new DessertFood("ไอศกรีมกะทิ", 40, 280, "", "หอมกะทิ ใส่ท็อปปิ้งได้", 3, true),
                new DessertFood("ชาไทยเย็นไข่มุก", 65, 350, "", "หวานมัน ดื่มแก้ง่วง", 4, true),
                new DessertFood("ขนมปังปิ้งเนยนม", 55, 400, "", "กรอบนอกนุ่มใน", 4, false),
                new DessertFood("เต้าฮวยฟรุตสลัด", 49, 260, "", "เบา ๆ สบายท้อง", 3, true),
                new DessertFood("ครัวซองต์อัลมอนด์", 79, 390, "", "มื้อเบา ๆ คู่กับกาแฟ", 3, false)
        );
    }
}
