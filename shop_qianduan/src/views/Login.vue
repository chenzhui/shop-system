<template>
  <div style="display:flex;flex-direction:column;">
    <div style="display:flex">
      <p>电话号码:</p>
      <input v-model="phone" type="text" >
    </div>
    <div style="display:flex;margin-top: 20px;">
      <p>密码:</p>
      <input v-model="password" type="text" >
    </div>

    <button @click="login" style="height:20px;width: 10%;margin-top:20px;">登录</button>
  </div>
</template>
<script >
import axios from "axios";
import VueCookies from "vue-cookies";
export default {
setup(){

},
data(){
  return{
    phone:"",
    password:""
  }
},
methods:{
  login(){
    const that=this
    axios.get('http://127.0.0.1:10010/uaa/rl/login',{params:{"phone":that.phone,password:that.password}}).then((res)=>{
      localStorage.setItem("token",res.data.data.token)
      if(res.data.statusCode=200){
        alert("登录成功")
      }
    })
  }
}
}

</script>

<style scoped>

</style>