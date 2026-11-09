package com.example.data.repository

import com.example.data.model.Question

object DefaultQuestions {

    fun getMathAndScienceQuestions(): List<Question> {
        return listOf(
            Question(
                id = "m1",
                number = 1,
                subject = "Matematika",
                topic = "Operasi Bilangan Bulat & Pecahan",
                text = "Dalam sebuah kompetisi matematika, skor untuk jawaban benar adalah 4, jawaban salah adalah -2, dan tidak dijawab adalah -1. Dari 40 soal yang diberikan, seorang siswa menjawab 32 soal benar dan 5 soal salah. Berapakah total skor yang diperoleh siswa tersebut?",
                options = listOf(
                    "A. 115",
                    "B. 118",
                    "C. 120",
                    "D. 125"
                ),
                correctAnswerIndex = 0,
                explanation = "Pembahasan:\n• Soal benar: 32 × 4 = 128\n• Soal salah: 5 × (-2) = -10\n• Soal tidak dijawab: (40 - 32 - 5) = 3 soal × (-1) = -3\n• Total Skor = 128 + (-10) + (-3) = 115.\nJawaban yang tepat adalah A."
            ),
            Question(
                id = "m2",
                number = 2,
                subject = "Matematika",
                topic = "Teorema Pythagoras & Geometri",
                text = "Sebuah tiang bendera setinggi 12 meter berdiri tegak di atas tanah datar. Sebuah kawat penguat diikatkan pada puncak tiang bendera menuju pasak di tanah yang berjarak 9 meter dari kaki tiang bendera. Berapakah panjang minimal kawat penguat tersebut?",
                options = listOf(
                    "A. 13 meter",
                    "B. 15 meter",
                    "C. 16 meter",
                    "D. 21 meter"
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\nGunakan Teorema Pythagoras c = √(a² + b²):\n• a (tinggi tiang) = 12 m\n• b (jarak kaki ke pasak) = 9 m\n• c = √(12² + 9²) = √(144 + 81) = √225 = 15 meter.\nJawaban yang tepat adalah B."
            ),
            Question(
                id = "m3",
                number = 3,
                subject = "Matematika",
                topic = "Sistem Persamaan Linear Dua Variabel (SPLDV)",
                text = "Harga 3 buah buku tulis dan 2 batang pensil di koperasi sekolah adalah Rp 17.500,00. Sedangkan harga 2 buah buku tulis dan 3 batang pensil adalah Rp 15.000,00. Jika Rian membeli 4 buah buku tulis dan 1 batang pensil, berapakah jumlah uang yang harus dibayar Rian?",
                options = listOf(
                    "A. Rp 19.500,00",
                    "B. Rp 20.000,00",
                    "C. Rp 21.000,00",
                    "D. Rp 22.500,00"
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\nMisal b = buku, p = pensil.\n(1) 3b + 2p = 17.500 (×2) => 6b + 4p = 35.000\n(2) 2b + 3p = 15.000 (×3) => 6b + 9p = 45.000\nEliminasi (2) - (1):\n5p = 10.000 => p = 2.000\nSubstitusi ke (1):\n3b + 2(2.000) = 17.500 => 3b = 13.500 => b = 4.500.\nHarga 4 buku + 1 pensil = 4(4.500) + 2.000 = 18.000 + 2.000 = Rp 20.000,00.\nJawaban yang tepat adalah B."
            ),
            Question(
                id = "m4",
                number = 4,
                subject = "Matematika",
                topic = "Statistika Rata-rata Gabungan",
                text = "Rata-rata nilai ulangan matematika 18 orang siswa putra adalah 74, sedangkan rata-rata nilai 22 orang siswa putri adalah 79. Berapakah rata-rata nilai gabungan seluruh siswa di kelas tersebut?",
                options = listOf(
                    "A. 76,25",
                    "B. 76,75",
                    "C. 77,25",
                    "D. 77,50"
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\n• Total nilai putra = 18 × 74 = 1.332\n• Total nilai putri = 22 × 79 = 1.738\n• Total nilai seluruh = 1.332 + 1.738 = 3.070\n• Total siswa = 18 + 22 = 40 siswa\n• Rata-rata gabungan = 3.070 / 40 = 76,75.\nJawaban yang tepat adalah B."
            ),
            Question(
                id = "m5",
                number = 5,
                subject = "Matematika",
                topic = "Aritmetika Sosial & Bunga Tunggal",
                text = "Ibu menabung di bank sebesar Rp 4.000.000,00 dengan suku bunga tunggal 9% per tahun. Setelah beberapa bulan, saldo tabungan ibu bertambah menjadi Rp 4.240.000,00. Berapa lama ibu menabungkan uangnya?",
                options = listOf(
                    "A. 6 bulan",
                    "B. 8 bulan",
                    "C. 9 bulan",
                    "D. 10 bulan"
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\n• Bunga yang diperoleh = Rp 4.240.000 - Rp 4.000.000 = Rp 240.000\n• Bunga setahun (12 bulan) = 9% × Rp 4.000.000 = Rp 360.000\n• Bunga per bulan = Rp 360.000 / 12 = Rp 30.000\n• Waktu menabung = Rp 240.000 / Rp 30.000 = 8 bulan.\nJawaban yang tepat adalah B."
            ),
            Question(
                id = "s1",
                number = 6,
                subject = "IPA",
                topic = "Fisika - Gerak Lurus Beraturan & Kecepatan",
                text = "Dua buah mobil A dan B bergerak berlawanan arah pada lintasan lurus saling mendekat. Mobil A bergerak dengan kelajuan konstan 20 m/s dan mobil B bergerak dengan kelajuan konstan 25 m/s. Jika jarak mula-mula kedua mobil adalah 900 meter, pada detik keberapakah kedua mobil tersebut akan berpapasan?",
                options = listOf(
                    "A. 15 detik",
                    "B. 18 detik",
                    "C. 20 detik",
                    "D. 25 detik"
                ),
                correctAnswerIndex = 2,
                explanation = "Pembahasan:\n• Kelajuan relatif kedua mobil = vA + vB = 20 m/s + 25 m/s = 45 m/s\n• Waktu berpapasan (t) = Jarak total / (vA + vB)\n• t = 900 m / 45 m/s = 20 detik.\nJawaban yang tepat adalah C."
            ),
            Question(
                id = "s2",
                number = 7,
                subject = "IPA",
                topic = "Biologi - Sistem Peredaran Darah Manusia",
                text = "Bagian jantung manusia yang memiliki dinding otot paling tebal dan berfungsi memompa darah bersih kaya oksigen (O₂) ke seluruh jaringan tubuh adalah...",
                options = listOf(
                    "A. Atrium dekster (Serambi kanan)",
                    "B. Ventrikel dekster (Bilik kanan)",
                    "C. Atrium sinister (Serambi kiri)",
                    "D. Ventrikel sinister (Bilik kiri)"
                ),
                correctAnswerIndex = 3,
                explanation = "Pembahasan:\nBilik kiri (ventrikel sinister) memompa darah bersih (kaya O₂) ke seluruh tubuh melalui pembuluh aorta, sehingga membutuhkan tekanan hidrostatik tinggi yang diimbangi dengan lapisan dinding otot miokardium paling tebal dibandingkan bilik kanan yang hanya memompa ke paru-paru.\nJawaban yang tepat adalah D."
            ),
            Question(
                id = "s3",
                number = 8,
                subject = "IPA",
                topic = "Fisika - Tekanan Hidrostatis",
                text = "Seorang penyelam menyelam di laut pada kedalaman 8 meter di bawah permukaan air. Jika massa jenis air laut adalah 1.025 kg/m³ dan percepatan gravitasi bumi 10 m/s², berapakah tekanan hidrostatis yang dialami oleh penyelam tersebut?",
                options = listOf(
                    "A. 82.000 Pa",
                    "B. 82.500 Pa",
                    "C. 84.000 Pa",
                    "D. 92.000 Pa"
                ),
                correctAnswerIndex = 0,
                explanation = "Pembahasan:\nRumus Tekanan Hidrostatis:\nPh = ρ × g × h\n• ρ (massa jenis) = 1.025 kg/m³\n• g (gravitasi) = 10 m/s²\n• h (kedalaman) = 8 m\n• Ph = 1.025 × 10 × 8 = 82.000 Pascal (N/m²).\nJawaban yang tepat adalah A."
            ),
            Question(
                id = "s4",
                number = 9,
                subject = "IPA",
                topic = "Kimia Dasar - Perubahan Fisika dan Kimia",
                text = "Perhatikan fenomena berikut:\n(1) Lilin meleleh saat dinyalakan\n(2) Besi pagar berkarat di musim hujan\n(3) Beras ditumbuk menjadi tepung\n(4) Singkong difermentasi menjadi tape\nFenomena yang tergolong perubahan kimia ditunjukkan oleh nomor...",
                options = listOf(
                    "A. (1) dan (3)",
                    "B. (1) dan (4)",
                    "C. (2) dan (3)",
                    "D. (2) dan (4)"
                ),
                correctAnswerIndex = 3,
                explanation = "Pembahasan:\nPerubahan kimia menghasilkan zat baru dengan komposisi kimia berbeda:\n• (2) Besi berkarat (oksidasi Fe menjadi Fe₂O₃.xH₂O) = Kimia\n• (4) Fermentasi singkong menjadi tape (enzimatik menghasilkan alkohol/asam) = Kimia\nSedangkan (1) lilin meleleh dan (3) beras jadi tepung adalah perubahan fisika karena tidak mengubah identitas zat asalnya.\nJawaban yang tepat adalah D."
            ),
            Question(
                id = "s5",
                number = 10,
                subject = "IPA",
                topic = "Biologi - Ekologi & Rantai Makanan",
                text = "Di sebuah ekosistem sawah terdapat rantai makanan: Padi -> Belalang -> Katak -> Ular -> Burung Elang. Jika petani secara intensif membasmi katak karena dianggap hama, dampak ekologis langsung yang akan terjadi adalah...",
                options = listOf(
                    "A. Populasi belalang meningkat dan populasi ular menurun",
                    "B. Populasi padi meningkat drastis",
                    "C. Populasi burung elang bertambah banyak",
                    "D. Populasi belalang dan padi menurun bersamaan"
                ),
                correctAnswerIndex = 0,
                explanation = "Pembahasan:\nKatak adalah predator belalang sekaligus mangsa ular. Jika katak dibasmi:\n1. Predator belalang berkurang sehingga populasi belalang melonjak (meningkat).\n2. Sumber makanan ular berkurang drastis sehingga populasi ular menurun.\nJawaban yang tepat adalah A."
            )
        )
    }

    fun getLiteracyAndSkolastikQuestions(): List<Question> {
        return listOf(
            Question(
                id = "l1",
                number = 1,
                subject = "Bahasa Indonesia",
                topic = "Literasi Teks Informasi & Ide Pokok",
                text = "Bacalah kutipan teks berikut:\n'Hutan bakau (mangrove) memegang peranan krusial sebagai benteng pertahanan alami kawasan pesisir. Akar tunjang yang kokoh dan saling bertaut mampu meredam energi gelombang laut serta memecah hantaman tsunami kecil. Selain itu, endapan lumpur di sekitar akar mangrove berfungsi sebagai penahan erosi tanah pantai (abrasi) dan habitat berkembang biaknya kepiting, udang, serta ikan-ikan kecil.'\nIde pokok paragraf tersebut adalah...",
                options = listOf(
                    "A. Jenis-jenis biota laut yang tinggal di pohon mangrove",
                    "B. Fungsi dan peranan penting hutan mangrove bagi kawasan pesisir",
                    "C. Ancaman abrasi pantai terhadap perkembangbiakan udang dan kepiting",
                    "D. Karakteristik bentuk akar tunjang pada tanaman mangrove"
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\nIde pokok tercantum pada kalimat utama di awal paragraf (deduktif): 'Hutan bakau (mangrove) memegang peranan krusial sebagai benteng pertahanan alami kawasan pesisir.' Kalimat berikutnya menjelaskan perincian fungsi tersebut (peredam gelombang, penahan abrasi, habitat biota).\nJawaban yang tepat adalah B."
            ),
            Question(
                id = "l2",
                number = 2,
                subject = "Bahasa Indonesia",
                topic = "Kebahasaan & Kata Baku EYD V",
                text = "Pilihlah kalimat yang menggunakan kata baku dan tanda baca yang tepat sesuai Pedoman Umum Ejaan Bahasa Indonesia (EYD V):",
                options = listOf(
                    "A. Hasil analisa laboratorium menunjukkan bahwa pasien memerlukan asupan gizi yang effisien.",
                    "B. Seluruh peserta try out diwajibkan mempraktikkan protokol ujian dengan tertib dan seksama.",
                    "C. Proktor mengkoordinir jalannya ujian agar tidak terjadi kekeliruan sistim.",
                    "D. Para siswa diharapkan dapat merubah kebiasaan belajar agar hasil nilai maksimal."
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\nBentuk baku:\n• Opsi B tepat: 'praktik' -> 'mempraktikkan', 'seksama' (saksama).\n• Opsi A salah karena 'analisa' (seharusnya analisis), 'effisien' (efisien).\n• Opsi C salah karena 'mengkoordinir' (mengoordinasi) dan 'sistim' (sistem).\n• Opsi D salah karena 'merubah' (seharusnya mengubah, dari kata dasar ubah).\nJawaban yang tepat adalah B."
            ),
            Question(
                id = "e1",
                number = 3,
                subject = "Bahasa Inggris",
                topic = "Reading Comprehension - Announcement",
                text = "Read the announcement below:\n'Attention to all Grade 9 students! The final preparation try-out for the National Competency Assessment will be conducted next Monday starting strictly at 07:30 AM in the multimedia lab. Please bring your student registration card and remember that mobile phones are prohibited inside the examination room.'\nWhat is the main requirement for students entering the exam room?",
                options = listOf(
                    "A. Bringing their personal mobile phones",
                    "B. Bringing their student registration card and leaving mobile phones outside",
                    "C. Arriving after 07:30 AM at the school library",
                    "D. Registering themselves directly to the teacher during exam"
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\nBerdasarkan teks: 'Please bring your student registration card and remember that mobile phones are prohibited inside the examination room.' Siswa harus membawa kartu registrasi siswa dan ponsel dilarang di dalam ruang ujian.\nJawaban yang tepat adalah B."
            ),
            Question(
                id = "e2",
                number = 4,
                subject = "Bahasa Inggris",
                topic = "Grammar - Conditional Sentences Type 1",
                text = "Complete the dialogue:\nTeacher: 'Why are you so focused on doing these practice exercises?'\nFarhan: 'If I ... hard every evening, I ... the highest academic score in the upcoming examination.'",
                options = listOf(
                    "A. studied - would achieve",
                    "B. study - will achieve",
                    "C. had studied - would achieve",
                    "D. am studying - achieved"
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\nKalimat ini menggunakan Conditional Type 1 (kenyataan masa depan / future possible event):\nRumus: If + Simple Present (S + V1/Vs), S + will + V1.\nMaka bentuk yang tepat adalah 'study' dan 'will achieve'.\nJawaban yang tepat adalah B."
            ),
            Question(
                id = "sk1",
                number = 5,
                subject = "Penalaran Skolastik",
                topic = "Deret Bilangan & Logika Numerik",
                text = "Tentukan dua angka selanjutnya dari pola barisan bilangan berikut:\n3, 6, 12, 15, 30, 33, ..., ...",
                options = listOf(
                    "A. 66, 69",
                    "B. 36, 72",
                    "C. 66, 70",
                    "D. 60, 63"
                ),
                correctAnswerIndex = 0,
                explanation = "Pembahasan:\nAnalisis pola operasi bergantian:\n• 3 ke 6: × 2\n• 6 ke 12: salah, mari cek: +3, ×2, +3, ×2:\n  3 (+3) = 6\n  6 (×2) = 12\n  12 (+3) = 15\n  15 (×2) = 30\n  30 (+3) = 33\n  33 (×2) = 66\n  66 (+3) = 69.\nPola berikutnya adalah × 2 lalu + 3, sehingga menghasilkan 66 dan 69.\nJawaban yang tepat adalah A."
            ),
            Question(
                id = "sk2",
                number = 6,
                subject = "Penalaran Skolastik",
                topic = "Silogisme & Logika Posisi",
                text = "Semua siswa kelas 9 yang mengikuti try out membawa pensil 2B.\nSebagian siswa yang membawa pensil 2B juga membawa kalkulator ilmiah.\nKesimpulan yang pasti benar adalah...",
                options = listOf(
                    "A. Semua siswa kelas 9 membawa kalkulator ilmiah",
                    "B. Sebagian siswa kelas 9 membawa kalkulator ilmiah",
                    "C. Tidak ada siswa kelas 9 yang membawa kalkulator ilmiah",
                    "D. Semua siswa yang membawa kalkulator ilmiah adalah siswa kelas 9"
                ),
                correctAnswerIndex = 1,
                explanation = "Pembahasan:\nPremis 1: Semua siswa kelas 9 (A) adalah pembawa pensil 2B (B).\nPremis 2: Sebagian pembawa pensil 2B (B) adalah pembawa kalkulator ilmiah (C).\nKesimpulan yang sah: Sebagian siswa kelas 9 membawa kalkulator ilmiah (A ∩ C ≠ ∅).\nJawaban yang tepat adalah B."
            )
        )
    }
}
