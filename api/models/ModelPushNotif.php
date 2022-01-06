<?php

include ('../api/Connection.php');
date_default_timezone_set('Asia/jakarta');

    class Notification{
        private $connect;
        private $_key;
        private $_client_id;
        private $_url;
        private $id_baru;
        private $message_tittle;
        private $data;

        function __construct(){
            $db = new connection_database;

            $this->connect = $db->db_connection();
            $this->_url = "https://fcm.googleapis.com/fcm/send";
            $this->_key = "AAAAWu43gIk:APA91bFlBGd2QUZVYsX1evobsUFns7jy5h3LihGhw2rU7yss6ZmnmSWZpxnqqj51oFm-CAZv0vKhCkggdbZ61gLG0UR0onxfyd0WZq6JTuDesfK1ezXYC4ywcbvLHQNGxJoX2JYTqlw3";
            $this->_client_id = "ADK";
            $this->message_tittle="TES";
        }

        function register($reg_id, $reg_device, $reg_device_id, $nip){

            try{
                $client = $this->_client_id;

                if($this->getRegister($reg_id,$client,$nip)){
                    $query = "UPDATE tbl_registrasi SET reg_time = NOW() WHERE reg_id = '$reg_id' AND reg_client = '$client' AND nip = '$nip' ";
                    $statement = $this->connect->prepare($query);
                    $statement->execute();
                }

                else{
                    $query = "INSERT INTO tbl_registrasi (reg_id, reg_device, reg_device_id, reg_time, reg_client, nip) VALUES ('$reg_id', '$reg_device', '$reg_device_id', NOW(), '$client', '$nip') ";
                    $statement = $this->connect->prepare($query);
                    $statement->execute();
                }

                $this->data = array(
                    "pesan" => "berhasil"
                );
                return $this->data;
            }

            catch(PDOException $e){
                $this->data = array(
                    "pesan" => "gagal"
                );
                return $this->data;
            }
            
        }

        function getRegister($reg_id, $client, $nip){
            $query = "SELECT COUNT(reg_id) as total FROM tbl_registrasi WHERE reg_id = '$reg_id' AND reg_client = '$client' AND nip = '$nip' ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

                    while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                        $data = $row['total'] > 0 ? true : false;
                    }
                    
                    return $data;

        }

        function send($title, $nip, $pesan){
            $query = "SELECT id FROM tb_request_log ORDER BY id DESC LIMIT 1 ";
            $statement = $this->connect->prepare($query);
            $statement->execute();
            $row = $statement->fetch(PDO::FETCH_ASSOC);
            $id = intval($row['id']);
            $id++;

            $id_baru = sprintf($id);
            $push_id = "PI-00".$id_baru;

            $message_tittle="Data Telah Masuk";
            $message['title']    = $message_tittle;
            $message['message']   = $pesan;

            $reg_id = $this->getRegId($this->_client_id, $nip);
            if($reg_id != ""){
                $this->request($reg_id,$title,json_encode($message),$nip,$push_id);
            }
        }

        function request($reg_id, $title, $message, $nip, $push_id){
            $reg_ids [] = $reg_id;

            $data = array('registration_ids' => $reg_ids,
                'data'              => array(
                    'title'         => $title,
                    'message'       => $message
                )
            );

            //http header
            $headers = array('Authorization: key=' . $this->_key,
                        'Content-Type: application/json');
            
            //curl connection
            $ch = curl_init();

            curl_setopt($ch, CURLOPT_URL, $this->_url);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));

            $response  = curl_exec($ch);

            curl_close($ch);

            $this->requestLog($title .' : '. $message, $response, $reg_id, $nip, $push_id);
        }

        function requestLog($msg, $response, $reg_id, $nip, $push_id){
            try{   
                if($this->getRequestLog($reg_id,$push_id)){
                    $query = "UPDATE tb_request_log SET rl_status = 0 , rl_time = NOW(), response = '".addslashes($response)."', rl_message = '".addslashes($msg)."' WHERE reg_id = '$reg_id' AND push_id = '$push_id' ";
                    $statement = $this->connect->prepare($query);
                    $statement->execute();
                }

                else{
                    $query = "INSERT INTO tb_request_log(rl_message, response, rl_time, reg_id, nip, push_id) VALUES ('".addslashes($msg)."', '".addslashes($response)."', NOW(), '$reg_id', '$nip', '$push_id') ";
                    $statement = $this->connect->prepare($query);
                    $statement->execute();
                }
                $this->data = array(
                    "pesan"  => "berhasil"
                );
                return $this->data;
            }

            catch(PDOException $e){
                    $this->data = array(
                        "pesan"  => "error"
                    );
                    return $this->data;
            }  
        }

        function getRequestLog($reg_id, $push_id){
            $query = "SELECT COUNT(*) as total FROM tb_request_log WHERE reg_id = '$reg_id' AND push_id = '$push_id' ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                $data = $row['total'] > 0 ? true : false;
            }

            return $data;
        }

        function getRegId($client,$nip){
            $query = "SELECT * FROM tbl_registrasi WHERE reg_client = '$client' AND nip = '$nip' ORDER BY reg_time DESC LIMIT 1 ";
            $statement = $this->connect->prepare($query);
            $statement->execute();

            while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                return $row['reg_id'];
            }
        }


    }


?>