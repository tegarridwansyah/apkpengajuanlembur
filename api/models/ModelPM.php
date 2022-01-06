<?php

include ('../api/Connection.php');
    date_default_timezone_set('Asia/Jakarta');

    class PM{
        private $connect;
        public $status;

        function __construct(){
            $db = new connection_database;

            $this->connect = $db->db_connection();
        }

        function auto_increment(){
            $query = "SELECT * FROM tb_pm ORDER BY id_pm DESC LIMIT 1";
            $statement = $this->connect->prepare($query);
            $statement->execute();
            $data = $statement->fetch(PDO::FETCH_ASSOC);
            return $data;   
        }

        function auto_incrementPM(){
            $query = "SELECT id_pm FROM tb_pm ORDER BY id_pm DESC LIMIT 1";
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
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=auto_incrementPM",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Terjadi kesalhan, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=auto_incrementPM",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }   
        }

        function fetch_datapm(){
            $query = "SELECT * FROM tb_pm ORDER BY id_pm";
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
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=fetch_datapm",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=fetch_datapm",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
            return $this->data;
        }

        function insert_pm($id_pm,$nama_pm){
            if(isset($_POST["nama_pm"])){
                $query = "INSERT INTO tb_pm(id_pm,nama_pm) VALUES (:id_pm,:nama_pm)";
                $statement = $this->connect->prepare($query);
                $statement->bindParam(":nama_pm", $nama_pm);
                $statement->bindParam(":id_pm", $id_pm);

                try{
                    if($statement->execute()){
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "Berhasil",
                            "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=insert_pm",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data;
                    }
                }

                catch(PDOException $e){
                        $data = array(
                            "status" => "error",
                            "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                            "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=insert_pm",
                            "time"   => date('H:i:s d-m-y')
                        );
                        return $data;
                }
            }
        }

        function fetch_singlepm($id_pm){
            $query = "SELECT * FROM tb_pm WHERE id_pm = '".$id_pm."' ";
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
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=fetch_singlepm&id_pm=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=fetch_singlepm&id_pm=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        }

        function update_pm($id_pm,$nama_pm){
            if(isset($_POST["id_pm"])){
                $query = "UPDATE tb_pm SET nama_pm = :nama_pm WHERE id_pm = :id_pm";
                $statement = $this->connect->prepare($query);
                $statement->bindParam(":nama_pm", $nama_pm);
                $statement->bindParam(":id_pm", $id_pm);

                try{
                    if($statement->execute()){
                        $data = array(
                            "status" => "ok",
                            "pesan"  => "berhasil",
                            "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=update_pm"
                        );
                        return $data;
                    }
                }

                catch(PDOException $e){
                        $data = array(
                            "status" => "error",
                            "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                            "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=update_pm"
                        );
                        return $data;
                }
            }
        }

        function delete_pm($id_pm){
            $query = "DELETE FROM tb_pengajuan WHERE leader = '".$id_pm."' ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            $query = "DELETE FROM tb_pm WHERE id_pm = '".$id_pm."' ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            try{
                if($statement->execute()){
                    $data = array(
                        "status" => "ok",
                        "pesan"  => "Berhasil",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=delete_pm&id_pm=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=delete_pm&id_pm=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
            return $this->data;
        }

        function search_id($nama_pm,$nama_divisi){
            $query = "SELECT tb_pm.id_pm, tb_divisi.id_divisi FROM tb_pm, tb_divisi WHERE (nama_pm = '$nama_pm' AND nama_divisi = '$nama_divisi')";
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
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=search_idpm&nama_pm=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
                }
            }

            catch(PDOException $e){
                    $data = array(
                        "status" => "error",
                        "pesan"  => "Kesalahan pada query, silahkan cek kembali",
                        "url"    => "http://localhost/apkpengajuanlembur/api/ControllerPM.php?action=search_idpm&nama_pm=",
                        "time"   => date('H:i:s d-m-y')
                    );
                    return $data;
            }
        }
    }

?>