<?php

namespace App\Http\Controllers;

use App\Post;
use App\PostLike;
use App\UserFollow;
use Carbon\Carbon;
use DateTime;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\File;
use Validator;
use App\User;
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\SMTP;
use PHPMailer\PHPMailer\Exception;

class UserController extends Controller
{

    public function login(Request $request){

        $rules = [
            'email' => 'required',
            'password' => 'required|min:8',
        ];

        $validator = Validator::make($request->all(), $rules);

        if($validator->fails()){
            return response()->json(["message" => "Noto'g'ri ma'lumotlar yuborildi", "error" => 1], 200);
        }

        $user = User::where("email", $request->email)->first();

        if($user === null){
            return response()->json(["message" => "Foydalanuvchi topilmadi.", "error" => 1], 200);
        }

        if($user->password !== md5($request->password)){
            return response()->json(["message" => "Parol xato kiritildi.", "error" => 1], 200);
        }

        $data["user"] = $user;
        $data["success"] = 1;
        $data["message"] = "Tizimga kirdingiz";

        return response()->json($data, 200);
    }

    public function resetPassword(Request $request){
        $rules = [
            'user_id' => 'required',
            'cpass' => 'required',
            'npass' => 'required|min:8',
        ];

        $validator = Validator::make($request->all(), $rules);

        if($validator->fails()){
            return response()->json(["message" => "Noto'g'ri ma'lumotlar yuborildi.", "error" => 1], 200);
        }

        $user = User::where("id", $request->user_id)->first();

        if($user === null){
            return response()->json(["message" => "Foydalanuvchi topilmadi.", "error" => 1], 200);
        }

        if($user->password !== md5($request->cpass)){
            return response()->json(["message" => "Joriy parol xato kiritildi.", "error" => 1], 200);
        }

        $user->password = md5($request->npass);
        $user->update();

        return response()->json(["message" => "Parol yangilandi!", "success" => 1, "new_password" => md5($request->npass)], 200);

    }


    public function register(Request $request){
        $rules = [
            'email' => 'required|email',
            'full_name' => 'required',
            'username' => 'required',
            'password' => 'required|min:8',
        ];

        $validator = Validator::make($request->all(), $rules);

        if($validator->fails()){
            return response()->json(["message" => "Kerakli ma'lumotlar topilmadi", "error" => 1], 200);
        }

        $user = User::where("email", $request->email)->first();

        if($user !== null){
            return response()->json(["message" => "Bu emailda akkount ochilgan!", "error" => 1], 200);
        }

        $username = User::where("username", $request->username)->first();

        if($username !== null){
            return response()->json(["message" => "Bu username band", "error" => 1], 200);
        }

        $user = new User();
        $user->username = $request->username;
        $user->full_name = $request->full_name;
        $user->password = md5($request->password);
        $user->email = $request->email;
        $user->status = "user";

        $user->save();

        $code = md5($user->id);

        //MAIL Sending
        $mail = new PHPMailer(true);
        try{

            //SMTP SETTINGS
            $mail->isSMTP();
            $mail->Host = env('MAIL_HOST');
            $mail->SMTPAuth =true;

            $mail->Username = env('MAIL_USERNAME');
            $mail->Password = env('MAIL_PASSWORD');
            $mail->Port = env('MAIL_PORT');
            $mail->SMTPSecure = 'ssl';

            $data["name"] = $request->name . " " . $request->surname;
            $data["link"] = route("activate.mail", ["mid" => $code]);


            $mail->setFrom(env('MAIL_USERNAME'), "Rahmat App");
            $mail->Subject = "Rahmat App - Ro'yxatdan o'tish";

            $message = view("mails.register_code", ["data" => $data])->render();

            $mail->MsgHTML($message);
            $mail->addAddress($request->email , $request->name);
            $mail->send();
        }catch(phpmailerException $e){
            return $e;
        }catch(Exception $e){
            return $e;
        }


        if($mail){
            return response()->json(["message" => "Aktivlashtirish havolasi pochtangizga yuborildi. Profilni faollashtirib, tizimga kirishingiz mumkin.", "success" => 1], 200);
        }else{
            return response()->json(["message" => "Email yuborilishida xatolik yuz berdi. Bu haqida xabar bering (info@programmer.uz)", "error" => 1], 200);
        }

    }

    public function resetPasswordAuth(Request $request){
        $rules = [
            'email' => 'required|email'
        ];

        $validator = Validator::make($request->all(), $rules);

        if($validator->fails()){
            return response()->json(["message" => "Kerakli ma'lumotlar topilmadi", "error" => 1], 200);
        }

        $user = User::where("email", $request->email)->first();

        if($user == null)
        {
            return response()->json(["message" => "Bu emailda ro'yxatdan o'tilmagan", "error" => 1], 200);
        }

        $code = route("password.reset", ["mid" => md5($user->id)]);

        $mail = new PHPMailer(true);
        try{

            //SMTP SETTINGS
            $mail->isSMTP();
            $mail->Host = env('MAIL_HOST');
            $mail->SMTPAuth =true;


            $mail->Username = env('MAIL_USERNAME');
            $mail->Password = env('MAIL_PASSWORD');
            $mail->Port = env('MAIL_PORT');
            $mail->SMTPSecure = 'ssl';

            /*    $mail->Username = env('MAIL_USERNAME');
               $mail->Password = env('MAIL_PASSWORD');
               $mail->Port = env('MAIL_PORT');
               $mail->SMTPSecure = 'ssl';*/

            $data["name"] = $user->givenName . " " . $request->familyName;
            $data["code"] = $code;



            $mail->setFrom(env('MAIL_USERNAME'), "Programmer UZ");
            $mail->Subject = "Rahmat App - Parolni tiklash";

            $message = view("mails.forgot_pass_mail", ["data" => $data])->render();

            $mail->MsgHTML($message);
            $mail->addAddress($user->email , $user->givenName);
            $mail->send();
        }catch(phpmailerException $e){
            return response()->json(["message" => "Kutilmagan xatolik: ". $e, "error" => 1], 200);
        }catch(Exception $e){
            return response()->json(["message" => "Kutilmagan xatolik: " . $e, "error" => 1], 200);
        }

        if($mail){
            return response()->json(["message" => "Parolni tiklash manzili emailingizga yuborildi", "success" => 1], 200);
        }
        else
            return response()->json(["message" => "Email yuborilishida xatolik yuz berdi", "error" => 1], 200);
    }

    public function getUserProfileData($id){

        $user = User::where("id", $id)->first();

        if($user == null)
        {
            return response()->json(["message" => "Foydalanuvchi topilmadi", "error" => 1], 200);
        }

        $user_following_count = UserFollow::where("user_id", $user->id)->count();
        $user_followers_count = UserFollow::where("following_user_id", $user->id)->count();

        $user_posts = Post::where("user_id", $user->id)->latest()->get();

        $user_likes_count = 0;
        foreach ($user_posts as $post){
            $likes = PostLike::where("post_id", $post->id)->count();
            $user_likes_count += $likes;
            $post->user = $user;
            $post->published_time =$post->created_at->format('d.m.Y H:i');

            $user_liked_post = PostLike::where("post_id", $post->id)->where("user_id", $user->id)->first();

            $is_liked = 0;
            if($user_liked_post === null){
                $is_liked = 0;
            }
            else{
                $is_liked = 1;
            }

            $post->is_liked = $is_liked;
        }

        $user->following_count = $user_following_count;
        $user->followers_count = $user_followers_count;
        $user->likes_count = $user_likes_count;


        $data = array();

        $data["success"] = 1;
        $data["user"] = $user;
        $data["posts"] = $user_posts;

        return response()->json($data, 200);
    }

    public function updateProfile(Request $request){
        $rules = [
            'full_name' => 'required',
            'username' => 'required'
        ];

        $validator = Validator::make($request->all(), $rules);

        if($validator->fails()){
            return response()->json(["message" => "Kerakli ma'lumotlar topilmadi", "error" => 1], 200);
        }

        $user = User::where("id", $request->user_id)->first();

        if($user == null)
        {
            return response()->json(["message" => "Foydalanuvchi topilmadi", "error" => 1], 200);
        }

        $username = User::where("id", "!=", $user->id)->where("username", $request->username)->exists();

        if($username)
        {
            return response()->json(["message" => "Username band, boshqa nom kiriting.", "error" => 1], 200);
        }

        $user->full_name = $request->full_name;
        $user->bio = $request->bio;
        $user->username = $request->username;

        $image_64 = $request->profile_photo; //your base64 encoded data

        if($image_64 !== null) {
            $imageName = time().'.'.'jpg';

            $path = public_path() . "/uploads/img/profiles/" . $imageName;

            file_put_contents($path, base64_decode($image_64));
            //$image->move(public_path("uploads/img/posts"), $imageName);

            $user_profile_photo = asset("uploads/img/profiles/") . "/". $imageName;

            if($user->photo !== null) {
             $current_photo = basename($user->photo);
             $file_loc = public_path() . "uploads/img/profiles/" . $current_photo;
             if(file_exists($file_loc)){
                 File::delete($file_loc);
             }
            }

            $user->photo = $user_profile_photo;
        }

        $user->update();

        return response()->json(["message" => "Ma'lumotlar yangilandi.", "success" => 1], 200);

    }

    public function getUsers(){
        $users = User::where("is_activate", 1)->inRandomOrder()->get();

        if($users === null){
            return response()->json(["message" => "Foydalanuvchilar topilmadi", "error" => 1], 200);
        }

        $data["success"] = 1;
        $data["users"] = $users;

        return response()->json($data, 200);

    }



    public function followUser(Request $request){
        $rules = [
            'user_id' => 'required',
            'user_follow_id' => 'required',
        ];

        $validator = Validator::make($request->all(), $rules);

        if($validator->fails()){
            return response()->json(["message" => "Kerakli ma'lumotlar topilmadi", "error" => 1], 200);
        }

        $user_follow = UserFollow::where("user_id", $request->user_id)->where("following_user_id", $request->user_follow_id)->first();

        if($user_follow !== null){
            $user_follow->delete();
            return response()->json(["message" => "unfollow", "success" => 1], 200);
        }
        else{
            $new_user_follow = new UserFollow();
            $new_user_follow->user_id = $request->user_id;
            $new_user_follow->following_user_id = $request->user_follow_id;
            $new_user_follow->save();

            return response()->json(["message" => "follow", "success" => 1], 200);
        }

    }



}