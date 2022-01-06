<?php

include ('../api/Connection.php');
    date_default_timezone_set('Asia/Jakarta');

    class Karyawan{
        private $connect;
        public $status;
        public $data= array();

        function __construct(){
            $db = new connection_database;

            $this->connect = $db->db_connection();
        }

        function auto(){
            $query = "SELECT nip FROM tb_pegawai ORDER BY nip DESC LIMIT 1";
            $statement = $this->connect->prepare($query);
            $statement->execute();
            $data = $statement->fetch(PDO::FETCH_ASSOC);
            return $data;   
        }

        function auto_increment(){
            $query = "SELECT nip FROM tb_pegawai ORDER BY nip DESC LIMIT 1";
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
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=auto_increment",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }  

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=auto_increment",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        }

        function login($username, $pass){
			$cek = 0;
               $query = "SELECT tb_login.nip, tb_login.username, tb_login.pass, tb_login.level_pegawai, tb_divisi.nama_divisi FROM tb_login 
                         JOIN tb_pegawai ON tb_pegawai.nip = tb_login.nip JOIN tb_divisi ON tb_divisi.id_divisi = tb_pegawai.id_divisi 
                         WHERE (tb_login.username = '$username' OR tb_login.nip = '$username') AND tb_login.pass = '$pass' "; 
                $statement = $this->connect->prepare($query);
            
            try{
                if($statement->execute()){
                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
						$cek++;
                    }

                    if($cek > 0){
						$data = array(
							"result" => $data,
							"status" => "ok",
							"pesan"  => "Berhasil",
							"url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=login",
							"time" => date('H:i:s d-m-y')
                    	);
					} else {
						$data = array(
							"result" => "",
							"status" => "error",
							"pesan"  => "Gagal",
							"url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=login",
							"time" => date('H:i:s d-m-y')
                    	);
					}
                }
                return $data;
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                    "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=login",
                    "time" => date('H:i:s d-m-y')
                );
                return $data;
            }

        }

        function fetch_datapegawai(){
        $query = "SELECT tb_pegawai.nip, tb_pegawai.nama, tb_pegawai.alamat, tb_pegawai.no_telp, tb_pegawai.gaji,
                 tb_divisi.nama_divisi FROM tb_pegawai INNER JOIN tb_divisi 
                 ON tb_pegawai.id_divisi = tb_divisi.id_divisi
                 ORDER BY tb_pegawai.nip";
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
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=fetch_datapegawai",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                    "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=fetch_datapegawai",
                    "time" => date('H:i:s d-m-y')
                );
                return $data;
            }

            return $this->data;
        }

        function insert_pegawai($nip, $nama, $alamat, $no_telp, $id_divisi, $gaji){
            if(isset($_POST["nama"])){

                if ($id_divisi == "DIV003") {
                    $level_pegawai = "ATASAN";
                }
                else if ($id_divisi == "DIV004") {
                    $level_pegawai = "HRD";
                }
                else {
                    $level_pegawai = "KARYAWAN";
                } 

                $query = "INSERT INTO tb_pegawai(nip, nama, alamat, no_telp, id_divisi, gaji) VALUES ('$nip', '$nama', '$alamat', '$no_telp', '$id_divisi', '$gaji')";
                $statement = $this->connect->prepare($query);
                $statement->bindParam(":nip", $nip);
                $statement->bindParam(":nama", $nama);
                $statement->bindParam(":alamat", $alamat);
                $statement->bindParam(":no_telp", $no_telp);
                $statement->bindParam(":id_divisi", $id_divisi);
                $statement->bindParam(":gaji", $gaji);
                $statement->execute();
          
                $query = "INSERT INTO tb_login(nip, username, pass, level_pegawai, id_divisi) VALUES ('$nip', '$nama', 'ttx123', '$level_pegawai', '$id_divisi')";
                $statement = $this->connect->prepare($query);
                $statement->bindParam(":nip", $nip);
                $statement->bindParam(":username", $nama);
                $statement->bindParam(":level_pegawai", $level_pegawai); 
                $statement->execute(); 

                try{
                    if($statement->execute()){
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "Berhasil",
                            "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=insert_pegawai",
                            "time" => date('H:i:s d-m-y')
                        );
                        return $data;
                    }
                }

                catch(PDOException $e){
                        $data = array(
                            "status" => "error",
                            "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                            "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=insert_pegawai",
                            "time" => date('H:i:s d-m-y')
                        );
                        return $data;
                } 
            }
        }

        function fetch_singlepegawai($nip){
            $query = "SELECT tb_pegawai.nip, tb_pegawai.nama, tb_pegawai.alamat, tb_pegawai.no_telp, 
                      tb_pegawai.id_divisi, tb_divisi.nama_divisi, tb_pegawai.gaji
                      FROM tb_pegawai INNER JOIN tb_divisi ON tb_pegawai.id_divisi = tb_divisi.id_divisi    
                      WHERE tb_pegawai.nip = '".$nip."'";
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
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=fetch_singlepegawai&nip=",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                    "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=fetch_singlepegawai&nip=",
                    "time" => date('H:i:s d-m-y')
                );
                return $data;
            }
            return $this->data;
        }

        function update_pegawai($nip,$nama,$alamat,$no_telp,$id_divisi,$gaji){
            if(isset($_POST["nip"])){   
                $query = "UPDATE tb_pegawai, tb_login SET tb_pegawai.nama=:nama, tb_pegawai.alamat=:alamat, 
                          tb_pegawai.no_telp=:no_telp, tb_pegawai.id_divisi=:id_divisi, tb_pegawai.gaji=:gaji, tb_login.username=:nama 
                          WHERE tb_pegawai.nip=tb_login.nip AND tb_pegawai.nip=:nip";
                $statement= $this->connect->prepare($query);
                $statement->bindParam(":nama", $nama);
                $statement->bindParam(":alamat", $alamat);
                $statement->bindParam(":no_telp", $no_telp);
                $statement->bindParam(":id_divisi", $id_divisi);
                $statement->bindParam(":gaji", $gaji);
                $statement->bindParam(":username", $nama);
                $statement->bindParam(":nip", $nip);

                try{
                    if($statement->execute()){
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "Berhasil",
                            "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=update_pegawai",
                            "time" => date('H:i:s d-m-y')
                        );
                        return $data;
                    }
                }
                catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=update_pegawai",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data;
                } 
            }
        }

        function delete_pegawai($nip){   

            $query = "DELETE FROM tb_login WHERE nip = '".$nip."'";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            $query = "DELETE FROM tb_pegawai WHERE nip = '".$nip."'";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            try{
                if($statement->execute()){
                    $data[] = array(
                        "status" => "ok",
                        "pesan"  => "Berhasil",
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=delete_pegawai&nip=",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                $data[] = array(
                    "status" => "error",
                    "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                    "url" => "http://localhost/apkpengajuanlembur/api/ControllerKaryawan.php?action=delete_pegawai&nip=",
                    "time" => date('H:i:s d-m-y')
                );
                return $data;
            } 
            return $this->data; 
        }

        
    }

?>