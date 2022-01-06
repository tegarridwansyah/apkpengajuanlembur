<?php

require_once ('models/ModelPengajuan.php');

$api_object = new Pengajuan;

$action = isset($_GET['action']) ? $_GET['action'] : $_POST['action'];

switch($action){

    case'auto_incrementpengajuan':
        $data = $api_object->auto_incrementpengajuan();
    break;

    case'fetch_otomatis':
        $data = $api_object->fetch_otomatis($_POST["nip"]);
    break;
    
    case'fetch_datapengajuan':
        $data = $api_object->fetch_datapengajuan();
    break;

    case'datapengajuan_menunggu':
        $data = $api_object->datapengajuan_menunggu();
    break;

    case'pengajuan_dikonfirmasi':
        $data = $api_object->pengajuan_dikonfirmasi();
    break;

    case'pengajuandikonfirmasi_perbulan':
        $data = $api_object->pengajuandikonfirmasi_perbulan($_GET["tanggal"]);
    break;

    case'insert_pengajuan':
        $data = $api_object->insert_pengajuan($_POST["id_pengajuan"],$_POST["nip"],$_POST["nama"],$_POST["divisi"],$_POST["hari"],
                $_POST["tanggal"],$_POST["jam_mulai"],$_POST["jam_selesai"],$_POST["leader"],$_POST["keterangan"],$_POST["tanggal_selesai"]);
    break;

    case'single_pengajuan':
        $data = $api_object->single_pengajuan($_GET["id_pengajuan"]);
    break;

    case'singlepengajuan_menunggu':
        $data = $api_object->singlepengajuan_menunggu($_GET["nip"]);
    break;

    case'singlepengajuan_dikonfirmasi':
        $data = $api_object->singlepengajuan_dikonfirmasi($_GET["nip"]);
    break;

    case'fetch_singlepengajuan':
        $data = $api_object->fetch_singlepengajuan($_POST["id_pengajuan"]);
    break;

    case'fetch_singlenip':
        $data = $api_object->fetch_singlenip($_GET["nip"]);
    break;

    case'update_pengajuan':
        $data = $api_object->insert_pengajuan($_POST["id_pengajuan"],$_POST["status"]);
    break;

    case'terima_pengajuan':
        $data = $api_object->terima_pengajuan($_POST["id_pengajuan"]);
    break;

    case'tolak_pengajuan':
        $data = $api_object->tolak_pengajuan($_POST["id_pengajuan"]);
    break;

    case'penggajian':
        $data = $api_object->penggajian($_POST["id_pengajuan"]);
    break;

    case'delete_pengajuan':
        $data = $api_object->delete_pengajuan($_GET["id_pengajuan"]);
    break;

    default:
    $data = array(
        "result" => "",
        "pesan"  => "error"
    );
    break;
}

echo json_encode($data);

?>