# Panduan Pengguna: Aplikasi Manajemen Finansial UMKM

## Prasyarat
Sebelum memulai, pastikan Anda memiliki:
- Android Studio
- Perangkat Virtual Android (AVD) atau Android Phone
- Internet yang memadai
- Download Project Versi terbaru di GitHub ini



## Cara Buka Project
1. **Salin URL GitHub Project**
   - Klik tombol <> Code → pilih HTTPS → Copy URL
   - https://github.com/alpinocode/ManajemenReportFinansialUmkm.git
2. **Buka Android Studio**
   - Jalankan Android Studio di PC/laptop kamu.
3. **Clone Project dari GitHub**
   - Di Android Studio, Klik File → New → Project from Version Control → Git
   - Paste URL GitHub yang tadi kamu salin ke kolom URL.
   - Pilih direktori lokal untuk menyimpan project.
   - Klik Clone.
4. **Tunggu Proses Download**
   - Android Studio akan otomatis meng-clone semua file project ke folder lokal.
   - Tunggu hingga semua file terdownload.
5. **Buka Project**
   - Setelah selesai clone, Android Studio biasanya langsung membuka project tersebut.
   - Kalau tidak, buka manual lewat: File → Open → pilih folder project hasil clone.
6. **Sync Gradle**
   - Tunggu Android Studio sync Gradle project.
   - Kalau ada tombol Sync Now muncul di atas, klik Sync Now.
   - Pastikan internet aktif karena mungkin perlu download library dependency dari build.gradle.
7. **Run / Build Project**
   - Pilih device emulator atau device Android kamu
   - Klik tombol Run ▶️ di Android Studio.

## Video Demo
Tonton video demo: [Tautan ke Video](https://youtube.com/shorts/caeJl8oK_ck?feature=share)

## Authors

- [@alpinocode](https://github.com/alpinocode)
- [@MrsAya](https://github.com/MrsAya)

## Flow Aplikasi

### 1. Login
1. **Pengguna membuka aplikasi**.
2. **Form Login** ditampilkan:
   - Input: Email/Username.
   - Input: Password.
4. **Validasi kredensial pengguna**:
   - Jika valid, pengguna diarahkan ke **Dashboard**.
   - Jika tidak valid, tampilkan notifikasi kesalahan.

---

### 2. Send Verification Email (Verif)
1. **Pengguna mendaftar atau login pertama kali**.
2. Sistem mengirimkan **Link Verifikasi** melalui email.
3. **Pengguna Klik Link Verifikasi**:
   - Jika Link valid, pengguna diverifikasi.
   - Jika tidak valid, pengguna tidak diverifikasi.
4. Login Ulang pada Aplikasi

---

### 3. Register
1. **Pengguna menekan Tulisan "Create An Account"**.
2. Pengguna mengisi data:
   - Nama.
   - Email/Username.
   - Password.
   - Password Konfirmasi.
3. Sistem menyimpan data pengguna ke basis data.
4. Sistem mengarahkan ke proses **Verifikasi**.

---

### 4. Add Stok/Product
1. **Pengguna membuka menu "Produk"**.
2. Klik tombol **"+"** pada kanan bawah.
3. Isi data barang:
   - Nama Suplier.
   - Nama Barang.
   - Code Barang.
   - Harga Beli.
   - Harga Jual.
   - Jumlah Stok.
   - Keterangan.
4. Sistem menyimpan data barang baru ke basis data.
5. Notifikasi: "Stok berhasil ditambahkan."

---

### 5. Update Stok/Product
1. **Pengguna membuka menu "Produk"**.
2. Pilih barang yang ingin diperbarui.
3. Perbarui data barang:
   - Nama Suplier (Opsional).
   - Nama Barang (Opsional).
   - Code Barang (Opsional).
   - Harga Beli (Opsional).
   - Harga Jual (Opsional).
   - Jumlah Stok.
   - Keterangan (Opsional).
4. Sistem memperbarui data di basis data.
5. Notifikasi: "Stok berhasil diperbarui."

---

### 6. Delete Stok/Product
1. **Pengguna membuka menu "Produk"**.
2. Pilih barang yang ingin dihapus.
3. Klik tombol **Ikon Tempat Sampah**.
4. Konfirmasi penghapusan.
5. Sistem menghapus data dari basis data.
6. Notifikasi: "Stok berhasil dihapus."

---

### 7. Add Transaksi
1. **Pengguna membuka menu "Tambah Transaksi"**.
2. Pilih barang yang ingin dibeli.
3. Masukan **"Jumlah Barang"** yang dibeli
4. Sistem menyimpan data transaksi ke basis data.
5. Notifikasi: "Transaksi berhasil ditambahkan."

---

### 8. Financial Report
1. **Pengguna membuka menu "Laporan Keuangan"**.
2. Sistem menampilkan ringkasan:
   - Total Pemasukan.
   - Total Pengeluaran.
   - Laba Bersih.
   - Total Pajak.
3. Pengguna dapat melihatnya dalam bentuk Pie Chart.

---

### 9. Bookkeeping
1. **Pengguna membuka menu "Pembukuan"**.
2. Sistem menampilkan data transaksi.

---

### 10. History Transaksi
1. **Pengguna membuka menu "Riwayat Transaksi"**.
2. Sistem menampilkan daftar transaksi:
   - Code Barang.
   - Nama Barang.
   - Jumlah Barang.
   - Tanggal Transaksi.
   - Total Harga.

---

### 11. Notifikasi
1. **Pengguna membuka menu "Notification"**.
2. Pemberitahuan untuk pengguna:
   - Ketika stok barang hampir habis.
3. Notifikasi ditampilkan di aplikasi.

---



```mermaid
graph TD
    A[Mulai] --> B[Login]
    B -->|Kredensial Valid| C[Dashboard]
    B -->|Kredensial Tidak Valid| D[Tampilkan Notifikasi Kesalahan]

    C --> E[Verifikasi Akun]
    E -->|Link Valid| C[Dashboard]
    E -->|Link Tidak Valid| D[Tampilkan Notifikasi Kesalahan]

    C --> F[Daftar]
    F --> G[Isi Form Pendaftaran]
    G --> H[Verifikasi Akun]

    C --> I[Tambah Stok]
    I --> J[Isi Data Barang]
    J --> K[Simpan ke Database]
    K --> L[Tampilkan Notifikasi Stok Berhasil Ditambahkan]

    C --> M[Update Stok]
    M --> N[Pilih Barang]
    N --> O[Perbarui Data Barang]
    O --> K

    C --> P[Hapus Stok]
    P --> Q[Pilih Barang]
    Q --> R[Konfirmasi Penghapusan]
    R --> S[Hapus Data dari Database]
    S --> T[Tampilkan Notifikasi Stok Berhasil Dihapus]

    C --> U[Tambah Transaksi]
    U --> V[Pilih Barang]
    V --> W[Masukan Jumlah Barang]
    W --> X[Simpan Transaksi ke Database]
    X --> Y[Tampilkan Notifikasi Transaksi Berhasil Ditambahkan]

    C --> Z[Laporan Keuangan]
    Z --> AA[Tampilkan Ringkasan: Pemasukan, Pengeluaran, Laba, Pajak]

    C --> AB[Pembukuan]
    AB --> AC[Tampilkan Data Transaksi]

    C --> AD[Riwayat Transaksi]
    AD --> AE[Tampilkan Daftar Transaksi]

    C --> AF[Notifikasi]
    AF --> AG[Stok Hampir Habis]
    AG --> AH[Tampilkan Pemberitahuan]
```
## Tools/Library Used

1. **Firebase Auth**  
   Used for secure user authentication, including login and registration.

2. **Firebase Realtime Database**  
   Enables real-time data storage and synchronization across all clients, useful for storing user or attendance data.

3. **MPAndroidChart (Pie Chart)**  
   An open-source Android charting library used to display data in a pie chart format.  
   📊 [GitHub - MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)

4. **CircleImageView**  
   A library for displaying user profile images in a circular shape.  
   👤 [GitHub - CircleImageView](https://github.com/hdodenhof/CircleImageView)
5. **Lottie**
   

## Preview App View
| Login | Register | Send Verification Email |
|-------|----------|--------------------------|
| ![Login](https://github.com/user-attachments/assets/8a36292c-0afc-4864-897e-4dcda60ce270) | ![Register](https://github.com/user-attachments/assets/c66a73ea-cc18-453a-b403-7c350bcb3972) | ![VerificationSend](https://github.com/user-attachments/assets/da6a6add-2ffb-4bd8-950d-18864b8d7b94) |

| Add Stock/Product | Update Stock/Product | Delete Stock/Product |
|-------------------|----------------------|-----------------------|
| ![addProduct](https://github.com/user-attachments/assets/90c8b216-df4a-4681-af19-879d901f1a72) | ![Update Product](https://github.com/user-attachments/assets/47dc014d-fbb3-45f3-84c0-6468942895bf) | ![Delete Product](https://github.com/user-attachments/assets/9de299b5-34be-46cb-aeb0-0d79ce7b5852) |


| Add Transaksi | Financial Report | Bookkeeping |
|-------------------|----------------------|-----------------------|
| ![Add Transaction](https://github.com/user-attachments/assets/f07abce1-65e5-454d-a864-372c13874ff1) | ![Finansial Report](https://github.com/user-attachments/assets/770fd2d1-5e3e-446a-8a5d-2c7f470509e4) | ![Bookeping](https://github.com/user-attachments/assets/0ce74637-c644-4d60-bcda-dc0146535fc1) |

| History Transaksi | Notifikasi |
|-------------------|----------------------|
| ![HistoryTransaksion](https://github.com/user-attachments/assets/eee15daa-53be-451f-97cb-d79ab507f6f2) | ![Notification](https://github.com/user-attachments/assets/d02d5ec6-3aa6-4462-849a-518bf31d5f02) |






