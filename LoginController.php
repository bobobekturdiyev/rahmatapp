<?php
namespace App\Http\Controllers;

use App\User;
use GuzzleHttp\Client;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

class LoginController extends Controller
{
    public function activateMail($mid){
        $user = User::where(DB::raw("md5(id)"), $mid)->first();
        if($user === null)
            return "Foydalanuvchi topilmadi yoki havola eskirgan bo'lishi mumkin";


        $user->is_activate = 1;
        $user->update();
        //Auth::login($user);
        return "Akkount faollashtirildi";
    }
}
