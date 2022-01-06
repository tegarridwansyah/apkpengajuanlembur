<?php

include ('../api/Connection.php');
    date_default_timezone_set('Asia/Jakarta');

    class Pengajuan{
        private $connect;

        function __construct(){
            $db = new connection_database;
            $this->connect = $db->db_connection();
        }

        function auto_idpengajuan(){
            $query = "SELECT * FROM tb_pengajuan ORDER BY id_pengajuan DESC LIMIT 1";
            $statement = $this->connect->prepare($query);
            $statement->execute();
            $data = $statement->fetch(PDO::FETCH_ASSOC);
            return $data;
        }

        function auto_incrementpengajuan(){
            $query = "SELECT id_pengajuan FROM tb_pengajuan ORDER BY id_pengajuan DESC LIMIT 1";
            $statement = $this->connect->prepare($query);
            
            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                    }
                    $data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=auto_incrementpengajuan",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=auto_incrementpengajuan",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        }

        function fetch_datapengajuan(){
            $query = "SELECT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pegawai.nama, tb_divisi.nama_divisi, tb_pengajuan.hari, DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, 
                     tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pengajuan.keterangan, tb_pm.nama_pm, tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai, '%d %b %Y') AS tanggal_selesai, 
                     tb_pengajuan.gaji FROM tb_pm JOIN tb_pengajuan ON tb_pengajuan.leader = tb_pm.id_pm JOIN tb_pegawai 
                     ON tb_pengajuan.nip = tb_pegawai.nip JOIN tb_divisi ON tb_pegawai.id_divisi = tb_divisi.id_divisi ORDER BY tb_pengajuan.nip";
         $statement = $this->connect->prepare($query);

            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                    }
                    $data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "Berhasil",
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_datapengajuan",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_datapengajuan",
                        "time"   => date('H:i:s d-m-y')
                    );

                    return $data;
            }
        }

        function datapengajuan_menunggu(){
            $query = "SELECT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pegawai.nama, tb_divisi.nama_divisi, tb_pengajuan.hari, DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, 
                     tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pengajuan.keterangan, tb_pm.nama_pm, tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai ,'%d %b %Y') AS tanggal_selesai, tb_pengajuan.gaji FROM tb_pm JOIN tb_pengajuan
                     ON tb_pengajuan.leader = tb_pm.id_pm JOIN tb_pegawai ON tb_pengajuan.nip = tb_pegawai.nip JOIN tb_divisi ON tb_pegawai.id_divisi = tb_divisi.id_divisi
                     WHERE tb_pengajuan.status = 'menunggu' ORDER BY tb_pengajuan.tanggal DESC";
            $statement = $this->connect->prepare($query);

            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                    }
                    $data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "Berhasil",
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=datapengajuan_menunggu",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=datapengajuan_menunggu",
                        "time"   => date('H:i:s d-m-y')
                    );

                    return $data;
            }
        }

        function pengajuan_dikonfirmasi(){
            $query = "SELECT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pegawai.nama, tb_divisi.nama_divisi, tb_pengajuan.hari, DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, 
                     tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pengajuan.keterangan, tb_pm.nama_pm, tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai ,'%d %b %Y') AS tanggal_selesai, tb_pengajuan.gaji FROM tb_pm JOIN tb_pengajuan
                     ON tb_pengajuan.leader = tb_pm.id_pm JOIN tb_pegawai ON tb_pengajuan.nip = tb_pegawai.nip JOIN tb_divisi ON tb_pegawai.id_divisi = tb_divisi.id_divisi
                     WHERE tb_pengajuan.status = 'disetujui' OR tb_pengajuan.status ='ditolak' ORDER BY tb_pengajuan.tanggal DESC";
            $statement = $this->connect->prepare($query);

            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                    }
                    $data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "Berhasil",
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=pengajuan_dikonfirmasi",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=pengajuan_dikonfirmasi",
                        "time"   => date('H:i:s d-m-y')
                    );

                    return $data;
            }
        }

        function pengajuandikonfirmasi_perbulan($tanggal){
            $query = "SELECT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pegawai.nama, tb_divisi.nama_divisi, tb_pengajuan.hari, DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, 
                     tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pengajuan.keterangan, tb_pm.nama_pm, tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai ,'%d %b %Y') AS tanggal_selesai, tb_pengajuan.gaji FROM tb_pm JOIN tb_pengajuan
                     ON tb_pengajuan.leader = tb_pm.id_pm JOIN tb_pegawai ON tb_pengajuan.nip = tb_pegawai.nip JOIN tb_divisi ON tb_pegawai.id_divisi = tb_divisi.id_divisi
                     WHERE (tb_pengajuan.status = 'disetujui' OR tb_pengajuan.status ='ditolak') AND month(tb_pengajuan.tanggal) = '".$tanggal."' ORDER BY tb_pengajuan.tanggal DESC";
            $statement = $this->connect->prepare($query);
			$cek = 0;
            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
						$cek++;
                    }
                    if($cek == 0){
						$data = array(
                        "result" => "Tidak ada data",
                        "status" => "error",
                        "pesan"  => "Tidak ada data",
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=pengajuan_dikonfirmasi",
                        "time" => date('H:i:s d-m-y')
                    );
					} else {
						$data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "Berhasil",
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=pengajuan_dikonfirmasi",
                        "time" => date('H:i:s d-m-y')
                    );
					}
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=pengajuan_dikonfirmasi",
                        "time"   => date('H:i:s d-m-y')
                    );

                    return $data;
            }
        }

        

        function insert_pengajuan($id_pengajuan,$nip,$nama,$divisi,$hari,$tanggal,$jam_mulai,$jam_selesai,$leader,$keterangan,$tanggal_selesai){
            if(isset($_POST["hari"])){
                //Penghitungan estimasi jam (selesih lama lembur)
                $mulai = $tanggal.$jam_mulai;
                $selesai = $tanggal_selesai.$jam_selesai;
                
                $start = new DateTime($mulai);
                $end = new DateTime($selesai);
                $diff = $start->diff($end);
                $estimasi_jam = $diff->h." Jam"." ".$diff->i." menit";

                //Penghitungan gaji ditambah gaji lembur
                $query = "SELECT tb_pegawai.gaji FROM tb_pegawai WHERE tb_pegawai.nip = '".$nip."' ";
                $statement= $this->connect->prepare($query);
                $statement->execute();
                $gaji = $statement->fetch(PDO::FETCH_ASSOC);
                $harga = ($gaji['gaji'] * 2 * $diff->h) + (($gaji['gaji'] / 60) * 2 * $diff->i);

                $query = "INSERT INTO tb_pengajuan (id_pengajuan, nip, nama, divisi, hari, tanggal, jam_mulai, jam_selesai, estimasi_jam, gaji, leader, 
                        keterangan, status, tanggal_selesai) VALUES ('$id_pengajuan', '$nip', '$nama', '$divisi', '$hari', '$tanggal', '$jam_mulai', '$jam_selesai',
                        '$estimasi_jam', '$harga', '$leader', '$keterangan', 'Menunggu', '$tanggal_selesai')"; 

                $statement = $this->connect->prepare($query);
                $statement->bindParam(":nip", $nip);
                $statement->bindParam(":nama", $nama);
                $statement->bindParam(":divisi", $divisi);
                $statement->bindParam(":hari", $hari);
                $statement->bindParam(":tanggal", $tanggal);
                $statement->bindParam(":jam_mulai", $jam_mulai);
                $statement->bindParam(":jam_selesai", $jam_selesai);
                $statement->bindParam(":estimasi_jam", $estimasi_jam);
                $statement->bindParam(":gaji", $harga);
                $statement->bindParam(":leader", $leader);
                $statement->bindParam(":keterangan", $keterangan);
                $statement->bindParam(":tanggal_selesai", $tanggal_selesai);

                try{
                    if($statement->execute()){
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "berhasil",
                            "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=insert_pengajuan",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data;
                    }
                    return $this->data;
                }

                catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=insert_pengajuan",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }
        }

        function single_pengajuan($id_pengajuan){
            $query ="SELECT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pengajuan.nama, tb_divisi.nama_divisi, tb_pengajuan.hari,
            DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pm.nama_pm, tb_pengajuan.keterangan, 
            tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai ,'%d %b %Y') AS tanggal_selesai, tb_pengajuan.gaji FROM tb_pengajuan, tb_pm, tb_divisi WHERE tb_pengajuan.id_pengajuan = '".$id_pengajuan."' 
            AND tb_divisi.id_divisi = tb_pengajuan.divisi AND tb_pm.id_pm = tb_pengajuan.leader";
            $statement = $this->connect->prepare($query);
            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                    }
                    $data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                    "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan",
                    "time"   => date('H:i:s d-m-y')
                );
                return $data;
            }
        } 

        function singlepengajuan_menunggu($nip){
            $query ="SELECT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pengajuan.nama, tb_divisi.nama_divisi, tb_pengajuan.hari,
            DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pm.nama_pm, tb_pengajuan.keterangan, 
            tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai ,'%d %b %Y') AS tanggal_selesai, tb_pengajuan.gaji FROM tb_pengajuan, tb_pm, tb_divisi WHERE tb_pengajuan.nip = '".$nip."' 
            AND tb_divisi.id_divisi = tb_pengajuan.divisi AND tb_pm.id_pm = tb_pengajuan.leader AND tb_pengajuan.status = 'menunggu' 
            ORDER BY tb_pengajuan.tanggal DESC ";
            $statement = $this->connect->prepare($query);
			$cek = 0;
            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
						$cek++;
                    }
					
					if($cek == 0){
						$data = null;
					} else {
						$data = array(
							"result" => $data,
							"status" => "ok",
							"pesan"  => "berhasil",
							"url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan",
							"time"   => date('H:i:s d-m-y')
						);
					}
					                  
                    return $data;
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                    "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan",
                    "time"   => date('H:i:s d-m-y')
                );
                return $data;
            }
        } 

        function singlepengajuan_dikonfirmasi($nip){
            $query ="SELECT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pengajuan.nama, tb_divisi.nama_divisi, tb_pengajuan.hari,
            DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pm.nama_pm, tb_pengajuan.keterangan, 
            tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai ,'%d %b %Y') AS tanggal_selesai, tb_pengajuan.gaji FROM tb_pengajuan, tb_pm, tb_divisi WHERE tb_pengajuan.nip = '".$nip."' 
            AND tb_divisi.id_divisi = tb_pengajuan.divisi AND tb_pm.id_pm = tb_pengajuan.leader 
            AND (tb_pengajuan.status = 'disetujui' OR tb_pengajuan.status = 'ditolak') ORDER BY tb_pengajuan.tanggal DESC ";
            $statement = $this->connect->prepare($query);
			$cek = 0;
            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
						$cek++;
                    }
                    
					if($cek == 0){
						$data = null;
					} else {
						$data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan",
                        "time"   => date('H:i:s d-m-y')
                    );	
					}
					
                    return $data;
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                    "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan",
                    "time"   => date('H:i:s d-m-y')
                );
                return $data;
            }
        } 

        function fetch_singlepengajuan($id_pengajuan){
            $query = "SELECT DISTINCT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pengajuan.nama, tb_divisi.nama_divisi, tb_pengajuan.hari,
            DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pm.nama_pm, tb_pengajuan.keterangan, 
            tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai ,'%d %b %Y') AS tanggal_selesai, tb_pengajuan.gaji FROM tb_pengajuan INNER JOIN tb_pm ON tb_pm.id_pm = tb_pengajuan.leader INNER JOIN tb_divisi 
            ON tb_divisi.id_divisi = tb_pengajuan.divisi WHERE tb_pengajuan.id_pengajuan = '".$id_pengajuan."' ORDER BY tb_pengajuan.tanggal DESC ";
            $statement = $this->connect->prepare($query);
            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                    }
                    $data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                    "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan",
                    "time"   => date('H:i:s d-m-y')
                );
                return $data;
            }
        }

        function fetch_singlenip($nip){
            $query = "SELECT DISTINCT tb_pengajuan.id_pengajuan, tb_pengajuan.nip, tb_pengajuan.nama, tb_divisi.nama_divisi, tb_pengajuan.hari,
            DATE_FORMAT(tb_pengajuan.tanggal ,'%d %b %Y') AS tanggal, tb_pengajuan.jam_mulai, tb_pengajuan.jam_selesai, tb_pengajuan.estimasi_jam, tb_pm.nama_pm, tb_pengajuan.keterangan, 
            tb_pengajuan.status, DATE_FORMAT(tb_pengajuan.tanggal_selesai ,'%d %b %Y') AS tanggal_selesai, tb_pengajuan.gaji FROM tb_pengajuan INNER JOIN tb_pm ON tb_pm.id_pm = tb_pengajuan.leader INNER JOIN tb_divisi ON tb_divisi.id_divisi = tb_pengajuan.divisi WHERE tb_pengajuan.nip = '".$nip."'"; 

            $statement = $this->connect->prepare($query);
            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                    }
                    $data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan&nip=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                    "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_singlepengajuan&nip=",
                    "time"   => date('H:i:s d-m-y')
                );
                return $data;
            }
        } 

        function fetch_otomatis($nip){
            $query = "SELECT tb_pegawai.nip, tb_pegawai.nama, tb_divisi.id_divisi  FROM tb_pegawai JOIN tb_divisi 
                    ON tb_pegawai.id_divisi = tb_divisi.id_divisi WHERE tb_pegawai.nip = '".$nip."'";
            $statement = $this->connect->prepare($query);

            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                    }
                    $data = array(
                        "result" => $data,
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_otomatis",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "result" => $data,
                        "status" => "error",
                        "pesan"  => "terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhosot/apkpengajuanlembur/api/ControllerPengajuan.php?action=fetch_otomatis",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        }

        function delete_pengajuan($id_pengajuan){
            $query = "DELETE FROM tb_pengajuan WHERE id_pengajuan = '".$id_pengajuan."' ";
            $statement = $this->connect->prepare($query);

            try{
                if($statement->execute()){
                    $data = array(
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=delete_pengajuan&nip=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPengajuan.php?action=delete_pengajuan&nip=",
                        "time"   => date('H:i:s d-m-y')   
                    );
                    return $data;
            }
        }

        function update_pengajuan($id_pengajuan,$status){
            if(isset($_POST["id_pengajuan"])){
                $query = "UPDATE tb_pengajuan SET status = :status WHERE id_pengajuan = :id_pengajuan ";
                $statement = $this->connect->prepare($query);
                $statement->bindParam(":id_pengajuan", $id_pengajuan);   
                $statement->bindParam(":status", $status);                       

                try{
                    if($statement->execute()){
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "berhasil",
                            "url"    => "",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data;
                    }
                }

                catch(PDOException $e){
                      $data = array(
                            "status" => "error",
                            "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                            "url"    => "",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data; 
                }
            }
        }

        function tolak_pengajuan($id_pengajuan){
            try{
                $status = "Ditolak";
                $this->update_pengajuan($id_pengajuan,$status);
                    $data = array(
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                        "url"    => "",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        
        }

        function terima_pengajuan($id_pengajuan){
                try{
                    $status = "Disetujui";
                    $this->update_pengajuan($id_pengajuan,$status);
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "berhasil",
                            "url"    => "",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data;
                }

                catch(PDOException $e){
                        $data = array(
                            "status" => "error",
                            "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                            "url"    => "",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data;
                }
            
        }


    }


?>