package com.andlib.lp.bean;



//@SuppressWarnings("rawtypes")
public class User {  //implements Comparable, Parcelable {
	//@Id(column = "id")
	private String  id;           // int 用户ID
	private String  mobile;      // string 用户手机号
	public  String  user_name;  // String 用户名
	private String  avatar;    // String 用户头像
	private String  qq;       // String QQ号
	private String  point;   // Int 积分
	private String  userpwd;//
	/*
        @Override
        public void writeToParcel(Parcel dest, int flags) { //根据容器和标签进行写数据
            // TODO Auto-generated method stub
            //调用存放读取数据的容器

            dest.writeString(id);//
            dest.writeString(mobile);
            dest.writeString(user_name);
            dest.writeString(avatar);
            dest.writeString(qq);
            dest.writeString(point);
            dest.writeString(userpwd);

        }
    */
	public User() {

	}
/*
	public User(Parcel parcel) {//赋值顺序依照,write顺序
		id = parcel.readString();
		mobile = parcel.readString();
		user_name = parcel.readString();
		avatar = parcel.readString();
		qq = parcel.readString();
		point = parcel.readString();
		userpwd = parcel.readString();

	}


	public static final Creator<User> CREATOR = new Creator<User>() {

		@Override
		public User[] newArray(int size) {//根据传入的值,创建对象数组,user类型
			// TODO Auto-generated method stub
			return new User[size];//返回该数组
		}

		@Override
		public User createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new User(source);
		}
	};
*/
	/**获取用户密码,无参数 返回string类型*/
	public String getUserpwd() {
		return userpwd;
	}
	/**设置用户密码*/
	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	/**用goole解析json数据,返回list形式的用户数据*/
	/*public static List<User> parseList(String s) {
		List<User> list = null;
		Type listType = new TypeToken<List<User>>() {
		}.getType();
		if (list == null) {
			list = new Gson().fromJson(s, listType);
		}
		return list;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
*/
}
