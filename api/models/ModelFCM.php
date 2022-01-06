<?php
class FCM
{
    private $_url;
    private $_key;
    private $_client_id;

    function FCM()
    {
        
        $this->ID_FILE = "C-FCM";
        $this->_client_id = "ADK";

        $this->_url 	= "https://fcm.googleapis.com/fcm/send";
        $this->_key 	= "ISI DARI ANDROID";
    }

    function send($title,$type,$indication,$pesan,$images,$push_id,$message_title=""){
		$message['push_type']   = $type;
		$message['title']       = $message_title;
		$message['message'] 	= $pesan;
		$message['images'] 	    = urldecode($images);

		$reg_id 	= $this->getRegId($this->_client_id,$indication);
		if($reg_id != ""){
			$this->request($reg_id,$title,json_encode($message),$images,$indication,$push_id);
		}
    }

    function request($reg_id, $title, $msg, $images = null, $indications = null, $push_id = ''){
        
		$fields = array('registration_ids'	=> $reg_id,
			'data' 				=> array(
				'title'	 		=> $title,
				'message'	 	=> $msg,
				'request_code'	=> $req_code,
				'images' 		=> "",
				'push_id' 		=> $push_id
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
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true );
		curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
		curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

		$response 	= curl_exec($ch);

		curl_close($ch);

		$this->requestLog($req_code, $title .' : '. $msg, $response, $reg_id, $indications, $push_id);

    }

    function requestLog($req_code, $msg, $response, $reg_ids, $indications, $push_id){
        global $db, $fcm_config;

        for ($i = 0; $i < sizeof($reg_ids); $i++) {
            $reg_id 		= $reg_ids[$i];
            $indication     = $indications[$i] != null ? $indications[$i] : "";

            if($this->getRequestLog($reg_id,$push_id)){
                $sql = "UPDATE tbl_fcm_request_log SET
                          rl_status = 0,
                          rl_time   = NOW(),
                          rl_response = '".addslashes($response)."',
                          rl_message = '".addslashes($msg)."'
                        WHERE
                            fcm_reg_id = '$reg_id' AND rl_push_id = '$push_id'

                          ";

                if (!$db->sql_query($sql)){
                    if(defined("API")){
                        return false;
                    }
                    die_error("Err:$this->ID_FILE".__LINE__);
                }
            }else{
                $sql = "INSERT INTO tbl_fcm_request_log SET
                          rl_code 		= '$req_code',
                          rl_message 	= '". addslashes($msg) ."',
                          rl_response 	= '". addslashes($response) ."',
                          rl_time  	= now(),
                          fcm_reg_id 	= '$reg_id',
                          rl_indication= '$indication',
                          rl_push_id 	= '$push_id'
                          ";

                if (!$db->sql_query($sql)){
                    if(defined("API")){
                        return false;
                    }
                    die_error("Err:$this->ID_FILE".__LINE__);
                }
            }

        }

        return true;
    }

    /*function getRequestLog($reg_id, $push_id){
        global $db;

        $sql = "SELECT
						COUNT(id) as total
					FROM
						tbl_fcm_request_log
					WHERE
						fcm_reg_id 		= '$reg_id' AND
						rl_push_id 		= '$push_id'
					";

        if (!$result = $db->sql_query($sql)){
            if(defined("API")){
                return false;
            }
            die_error("Err:$this->ID_FILE".__LINE__);
        }

        $row = $db->sql_fetchrow($result);
        $res = $row['total'] > 0 ? true : false;

        return $res;
    } */

    function register($reg_id, $device, $device_id, $indication){
        global $db;
        $client = $this->_client_id;

        if($this->getRegister($reg_id,$client,$indication)){
            $sql = "UPDATE tbl_fcm_registration SET
                          fcm_reg_time   = NOW()
                      WHERE
                          fcm_reg_id = '$reg_id' AND fcm_reg_client = '$client' AND fcm_reg_indication = '$indication'
                          ";

            if (!$db->sql_query($sql)){
                if(defined("API")){
                    return false;
                }
                die_error("Err:$this->ID_FILE".__LINE__);
            }
        }else{
            $sql = "INSERT INTO tbl_fcm_registration SET
                      fcm_reg_id 			= '$reg_id',
                      fcm_reg_device 		= '$device',
                      fcm_reg_device_id	    = '$device_id',
                      fcm_reg_time  		= now(),
                      fcm_reg_client  		= '$client',
                      fcm_reg_indication	= '$indication'
                        ";

            if (!$db->sql_query($sql)){
                if(defined("API")){
                    return false;
                }
                die_error("Err:$this->ID_FILE".__LINE__);
            }
        }

        return true;
    }

    function getRegister($reg_id, $client, $indication){
        global $db;

        $sql = "SELECT
						COUNT(fcm_reg_id) as total
					FROM
						tbl_fcm_registration
					WHERE
						fcm_reg_id 			= '$reg_id' AND
						fcm_reg_client 		= '$client' AND
						fcm_reg_indication 	= '$indication'
					";

        if (!$result = $db->sql_query($sql)){
            if(defined("API")){
                return false;
            }
            die_error("Err:$this->ID_FILE".__LINE__);
        }

        $row = $db->sql_fetchrow($result);
        $res = $row['total'] > 0 ? true : false;

        return $res;
    }

    /*function getDetailLogByRegId($reg_id, $push_id){
        global $db;
        $sql = "SELECT * FROM tbl_fcm_request_log
				WHERE
					id 		= '$push_id'";

        if (!$result = $db->sql_query($sql)){
            if(defined("API")){
                return false;
            }
            die_error("Err:$this->ID_FILE".__LINE__);
        }

        $row = $db->sql_fetchrow($result);

        return $row;
    } */

    function getRegId($client,$indication){
        global $db;
        $sql = "SELECT * FROM tbl_fcm_registration
				WHERE
					fcm_reg_client 		= '$client' AND
					fcm_reg_indication 	= '$indication'
				ORDER BY
					fcm_reg_time DESC
				LIMIT 1";

        if (!$result = $db->sql_query($sql)){
            if(defined("API")){
                return false;
            }
            die_error("Err:$this->ID_FILE".__LINE__);
        }

        $row = $db->sql_fetchrow($result);

        return $row['fcm_reg_id'];
    }

    /*function updateStatusReceiveRequestLog($reg_id,$push_id){
        global $db;

        $sql = "UPDATE tbl_fcm_request_log SET
                          rl_status   = 1,
                          rl_receive_time   = NOW()
                      WHERE
                          id = '$push_id'
                          ";

        if (!$db->sql_query($sql)){
            if(defined("API")){
                return false;
            }
            die_error("Err:$this->ID_FILE".__LINE__);
        }

        return true;
    } */

    /*function updateStatusReadRequestLog($reg_id,$push_id){
        global $db;

        $sql = "UPDATE tbl_fcm_request_log SET
                          rl_status   = 2,
                          rl_read_time   = NOW()
                      WHERE
                          id = '$push_id'
                          ";

        if (!$db->sql_query($sql)){
            if(defined("API")){
                return false;
            }
            die_error("Err:$this->ID_FILE".__LINE__);
        }

        return true;
    } */

    /*function getInbox($no_telp){
        global $db;
        $sql = "SELECT * FROM tbl_fcm_request_log
				WHERE
					rl_indication 		= '$no_telp' order by id desc";

        if (!$result = $db->sql_query($sql)){
            if(defined("API")){
                return false;
            }
            die_error("Err:$this->ID_FILE".__LINE__);
        }

        //$row = $db->sql_fetchrow($result);

        return $result;
    } */
    
    /*function getAllRegId($client){
        global $db;
        $sql = "SELECT * FROM tbl_fcm_registration
				WHERE
					fcm_reg_client 		= '$client' AND
					fcm_reg_indication 	!= 0
				ORDER BY
					id ASC";

        if (!$result = $db->sql_query($sql)){
            if(defined("API")){
                return false;
            }
            die_error("Err:$this->ID_FILE".__LINE__);
        }

        $data = array();

        while($row = $db->sql_fetchrow($result)){
            $data[] = $row;
        }

        return $data;
    } */

}