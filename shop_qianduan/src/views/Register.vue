<template>
  <div style="display:flex;flex-direction: column;">
    <div style="display:flex;">
      <p>电话号码:</p>
      <input v-model="phone" type="text" >
    </div>
    <div style="display:flex;">
      <p>密码:</p>
      <input v-model="password" type="text" >
    </div>
    <div style="display:flex;">
      <p>昵称:</p>
      <input v-model="nickName" type="text" >
    </div>


    <button @click="register" style="height:20px;width:10%;margin-top:20px;">注册</button>
  </div>
</template>

<script >
import axios from "axios";
export default {
  data(){
    return{
      phone:"",
      password:"",
      nickName: "",
    }
  },
  methods:{
    register(){
      const that=this
      axios.get('http://127.0.0.1:10010/uaa/rl/register',{params:{"phone":that.phone,"password":that.password,"nickName":that.nickName}},).then((res)=>{
        if(res.data.statusCode=200){
          alert("注册成功")
        }
      })
    }
  }
}
</script>

<style scoped>

</style>