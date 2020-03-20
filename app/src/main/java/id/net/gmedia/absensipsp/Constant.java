package id.net.gmedia.absensipsp;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    public final static String TAG = "absensi_log";

    public static String EXTRA_BERITA = "berita";
    public static String EXTRA_REGISTER = "register";
    public static String EXTRA_TANGGAL = "tanggal";
    public static String EXTRA_APPROVAL = "approval";
    public static String EXTRA_FLAG_CUTI_KHUSUS = "cuti_khusus";

    public static String BaseUrl = "https://office.putmasaripratama.co.id/arjuna/rest/";
    //public static String BaseUrl = "http://192.168.20.37/arjuna/rest/";

    public static String urlLogin = BaseUrl + "authentication";
    public static String urlUangLembur = BaseUrl + "money";
    public static String urlCheckIn = BaseUrl + "scan";
    public static String urlRekapitulasiAbsensi = BaseUrl + "rekap_absen";
    public static String urlCuti = BaseUrl + "cuti";
    public static String urlFotoProfil = BaseUrl + "foto_profil";
    public static String urlHistory = BaseUrl + "view_cuti";
    public static String urlHistoryCutiKhusus = BaseUrl + "view_cuti_khusus";
    public static String urlDashboard = BaseUrl + "dashboard";
    public static String urlSisaCuti = BaseUrl + "sisa_cuti";
    public static String urlBiodata = BaseUrl + "biodata";
    public static String urlNews = BaseUrl + "news";
    public static String urlBatalCuti = BaseUrl + "batal_cuti";
    public static String urlBatalCutiKhusus = BaseUrl + "batal_cuti_khusus";
    public static String urlIpPublic = "https://erpsmg.gmedia.id/check_ip.php";
    public static String urlDownloadPDFRekapAbsensi = "https://erpsmg.gmedia.id/hrd/download_excel/rekap_absen?";
    public static String urlDownloadPDFRekapitulasiAbsensi = "https://erpsmg.gmedia.id/hrd/download_file/download_rekap_absen?";
    public static String urlJadwal = BaseUrl + "penjadwalan";
    public static String urlGantiPassword = BaseUrl + "reset_password";
    public static String urlProfile = BaseUrl + "biodata";
    public static String urlTotalTerlambat = BaseUrl + "detail_terlambat";
    public static String urlRekapAbsen = BaseUrl + "absensi";
    public static String urlIjinPulangAwal = BaseUrl + "create_pulang_awal";
    public static String urlIjinKeluarKantor = BaseUrl + "create_keluar_kantor";
    public static String urlViewPulangAwal = BaseUrl + "view_pulang_awal";
    public static String urlViewKeluarKantor = BaseUrl + "view_leave_work";
    public static String urlUpVersion = BaseUrl + "latest_version";
    public static String urlScanLog = BaseUrl + "scanlog";
    public static String urlMasterApproval = BaseUrl + "master_approval";
    public static String urlKonfirmasiApproval = BaseUrl + "approve_action";
    public static String urlChangeLog = BaseUrl + "changelog";
	public static String urlEditPIN = BaseUrl + "update_pin";
	public static String urlCreatePIN = BaseUrl + "create_pin";
	public static String urlMangkir = BaseUrl + "view_mangkir";
    public static String urlSp = BaseUrl + "view_sp";
    public static String urlApproval = BaseUrl + "view_approval";
    public static String urlRegister = BaseUrl + "register";
    public static String urlForgotPassword = BaseUrl + "forgot_password";
    public static String urlGaji = BaseUrl + "payroll";
    public static String urlMasterCutiKhusus = BaseUrl + "list_tipe_cuti_khusus";
    public static String urlCutiKhusus = BaseUrl + "cuti_khusus";
    public static String urlHistoryKlarifikasi = BaseUrl + "view_klarifikasi";
    public static String urlKlarifikasiAbsensi = BaseUrl + "klarifikasi_absensi";
    public static String urlMasterKlarifikasi = BaseUrl + "list_tipe_klarifikasi";
    public static  String urlNotifikasi = BaseUrl + "notification_list";
    public static  String urlFilterLembur = BaseUrl + "money_overtime";

    public static Map<String, String> getTokenHeader(Context context){
        SessionManager session = new SessionManager(context);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Consumer-id", "absensi");
        headers.put("Secret-key", "986bf02c97e4738cff389ec0b3d784bc");
        headers.put("Nip", session.getNip());
        headers.put("Token", session.getToken());
        return headers;
    }
}
