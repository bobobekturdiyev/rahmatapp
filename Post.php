<?php

namespace App;

use Carbon\Carbon;
use Illuminate\Database\Eloquent\Model;

class Post extends Model
{
    //
    public function post_like(){
        return $this->hasMany("App\PostLike", 'post_id');
    }

}
