<?php

namespace App\Http\Controllers;

use App\Post;
use App\PostLike;
use App\User;
use Illuminate\Support\Facades\Validator;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class PostController extends Controller
{
    public function createPost(Request $request){
        $rules = [
            'post_text' => 'required',
            'user_id' => 'required',
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

        $image_64 = $request->post_image; //your base64 encoded data

        $post = new Post();
        $post->user_id = $request->user_id;
        $post->content = $request->post_text;

        if($image_64 !== null) {
            $imageName = time().'.'.'jpg';

            $path = public_path() . "/uploads/img/posts/" . $imageName;

            file_put_contents($path, base64_decode($image_64));
            //$image->move(public_path("uploads/img/posts"), $imageName);

            $post_image_url = asset("uploads/img/posts/") . "/". $imageName;
            $post->photo = $post_image_url;
        }

        $post->save();

        return response()->json(["message" => "Post qo'shildi", "success" => 1], 200);

    }

    public function likePost(Request $request)
    {
        $rules = [
            'post_id' => 'required',
            'user_id' => 'required',
        ];

        $validator = Validator::make($request->all(), $rules);

        if($validator->fails()){
            return response()->json(["message" => "Kerakli ma'lumotlar topilmadi", "error" => 1], 200);
        }

        $post_like = PostLike::where("user_id", $request->user_id)->where("post_id", $request->post_id)->first();

        if($post_like === null){
            $like = new PostLike();
            $like->user_id = $request->user_id;
            $like->post_id = $request->post_id;
            $like->save();
            return response()->json(["message" => "on", "success" => 1], 200);
        }
        else {
            $post_like->delete();
            return response()->json(["message" => "off", "success" => 1], 200);
        }

    }
}
