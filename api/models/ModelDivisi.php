<?php

include ('../api/Connection.php');
date_default_timezone_set('Asia/Jakarta');

    class Divisi{
        private $connect;

        function __construct(){
            $db = new connection_database;

            $this->connect = $db->db_connection();
        }

        function auto_increment(){
            $query = "SELECT * FROM tb_divisi ORDER BY id_divisi DESC LIMIT 1";
            $statement = $this->connect->prepare($query);
            $statement->execute();
            $data = $statement->fetch(PDO::FETCH_ASSOC);
            return $data;   
        }

        function auto_incrementdivisi(){
            $query = "SELECT id_divisi FROM tb_divisi ORDER BY id_divisi DESC LIMIT 1";
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
                        "url"    => "http://localhost/apkpengajuan/api/ControllerDivisi.php?action=auto_incrementdivisi",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }  

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuan/api/ControllerDivisi.php?action=auto_incrementdivisi",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        }

        function fetch_datadivisi(){
            $query = "SELECT * FROM tb_divisi ORDER BY id_divisi";
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
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=fetch_datadivisi",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                    "url" => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=fetch_datadivisi",
                    "time" => date('H:i:s d-m-y')
                );
                return $data;
            }

            return $this->data;
        }

        function insert_divisi($id_divisi, $nama_divisi){
            if(isset($_POST["nama_divisi"])){
                $query = "INSERT INTO tb_divisi(id_divisi, nama_divisi) VALUES (:id_divisi,:nama_divisi)";
                $statement = $this->connect->prepare($query);
                $statement->bindParam(":id_divisi", $id_divisi);
                $statement->bindParam(":nama_divisi", $nama_divisi);

                try{
                    if($statement->execute()){
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "Berhasil",
                            "url" => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=insert_divisi",
                            "time" => date('H:i:s d-m-y')
                        );
                        return $data;
                    }
                }

                catch(PDOException $e){
                        $data = array(
                            "status" => "error",
                            "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                            "url" => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=insert_divisi",
                            "time" => date('H:i:s d-m-y')
                        );
                        return $data;
                }
                return $this->data;
            }
        }

        function fetch_singledivisi($id_divisi){
            $query = "SELECT * FROM tb_divisi WHERE id_divisi = '".$id_divisi."'";
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
                        "url" => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=fetch_singledivisi",
                        "time" => date('H:i:s d-m-y')
                    );
                    return $data; 
                }
            }

            catch(PDOException $e){
                $data = array(
                    "status" => "error",
                    "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                    "url" => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=fetch_singledivisi",
                    "time" => date('H:i:s d-m-y')
                );
                return $data;
            }
            return $this->data;
        }

        function update_divisi($id_divisi, $nama_divisi){
            if(isset($_POST["id_divisi"])){
                $query = "UPDATE tb_divisi SET nama_divisi = :nama_divisi WHERE id_divisi = :id_divisi"; 
                $statement = $this->connect->prepare($query);
                $statement->bindParam(":nama_divisi", $nama_divisi);
                $statement->bindParam(":id_divisi", $id_divisi);

                try{
                    if($statement->execute()){
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "berhasil",
                            "url"    => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=update_divisi&id_divisi=",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data;
                    }
                    return $this->data;
                }

                catch(PDOException $e){
                        $data = array(
                            "status" => "error",
                            "pesan"  => "kesalahan",
                            "url"    => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=update_divisi&id_divisi=",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data;
                }
            }
        }

        function delete_divisi($id_divisi){ 

            $query = " DELETE FROM tb_login WHERE id_divisi = '".$id_divisi."' ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            $query = " DELETE FROM tb_pengajuan WHERE divisi = '".$id_divisi."' ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            $query = " DELETE FROM tb_pegawai WHERE id_divisi = '".$id_divisi."' ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            $query = " DELETE FROM tb_divisi WHERE id_divisi = '".$id_divisi."' ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            try{
                if($statement->execute()){
                    $data = array(
                        "status" => "ok",
                        "pesan"  => "berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=delete_divisi&id_divisi=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=delete_divisi&id_divisi=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        }

        function search_iddivisi($nama_divisi){
            $query = "SELECT id_divisi FROM tb_divisi WHERE nama_divisi= '".$nama_divisi."'";
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
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=search_iddivisi&nama_divisi=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Terjadi kesalahan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerDivisi.php?action=search_iddivisi",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        }

    }



?>