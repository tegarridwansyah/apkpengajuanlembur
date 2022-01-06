<?php
    class connection_database{
        private $connect;

        public function db_connection(){
            try{
                $this->connect = new PDO("mysql:host=localhost; dbname=database_tiketux", "root", "");
                $this->connect->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);
                return $this->connect;
            }

            catch(PDOException $e){
                $e->getMessage();
            }
        }
    }

?>