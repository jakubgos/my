# oauth

### 1. Initialize LoginService
```
LoginService loginService = new LoginService.Builder()
                .setFactory(new LoginServiceComponentFactory(this))
                .setCallback(new Callback() {
                    @Override
                    public void onSuccess() {
                        SharedPreferences sharedPreferences = getSharedPreferences("token_store", Context.MODE_PRIVATE);
                        Log.d("...", "access token " + sharedPreferences.getString("access_token", ""));
                        Log.d("...", "refresh token " + sharedPreferences.getString("refresh_token", ""));
                        Log.d("...", "user token " + sharedPreferences.getString("user", ""));
                        Log.d("...", "password token " + sharedPreferences.getString("password", ""));
                        Log.d("...", "user id " + sharedPreferences.getLong("user_id", -1));
                        toolbar.setTitle(sharedPreferences.getString("user_name", ""));
                    }

                    @Override
                    public void onFailure() {
                        Log.d("...", "Failed");
                    }
                })
                .build();
```
### 2. Login
```
...
loginService.login(login, password);
...
```
### 3. Initilialize TrackerService
```
TrackerService trackerService = new TrackerService.Builder()
                .setFactory(new TrackerServiceComponentFactory(this))
                .build();
```

### 4. Send data
```
...
CoordinatesVO coordinatesVO = new CoordinatesVO();
coordinatesVO.setLng(latValue);
coordinatesVO.setLat(lngValue);
coordinatesVO.setCreationDate(date);

trackerService.sendCoordinates(coordinatesVO, new Callback() {
  @Override
  public void onSuccess() {
    Log.i("...", "sent !");
  }

  @Override
  public void onFailure() {
    Log.i("...", "error");
  }
});
...
```
#### Happy coding !
