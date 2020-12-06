<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/


Route::post('user/login', [
    'uses' => "UserController@login"
]);

Route::post('user/register', [
    'uses' => "UserController@register"
]);

Route::post('user/reset/password', [
    'uses' => "UserController@resetPassword"
]);

Route::post('user/reset/password/auth', [
    'uses' => "UserController@resetPasswordAuth"
]);

Route::get("getUserProfileData/{id}", 'UserController@getUserProfileData');


Route::post('createPost', [
    'uses' => "PostController@createPost"
]);

Route::post('updateProfile', [
    'uses' => "UserController@updateProfile"
]);

Route::post('likePost', [
    'uses' => "PostController@likePost"
]);

Route::get("getUsers", 'UserController@getUsers');

Route::post('followUser', [
    'uses' => "UserController@followUser"
]);
