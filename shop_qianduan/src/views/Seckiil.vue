<template>
  <div style="display:flex;">
    <div v-for="item in seckillProducts" :key="item.seckillId" style="display:flex;flex-direction:column;margin-left:10%;">
      <p>{{item.productName}}</p>
      <p>{{item.productTitle}}</p>
      <p>{{item.productDetail}}</p>
      <p>{{item.productPrice}}</p>
      <p>{{item.seckillPrice}}</p>
      <p>{{item.intergral}}</p>
      <p>{{item.stockCount}}</p>
      <button @click="makeOrder(item.seckillId)" style="height:20px;">点击秒杀</button>
    </div>
  </div>
</template>

<script>
import {getCurrentInstance, onMounted} from "vue";
import axios, {Axios} from "axios";
import router from "@/router/First.js";
export default {
setup(){
  const gci=getCurrentInstance()
  onMounted(()=>{
    //const hour=new Date().getHours().toString().padStart(2,'0');//获取当前时间的几时
    //gci.data.time=router.currentRoute.value.query.time
    gci.data.time=18
    axios.get("http://127.0.0.1:10010/seckill/seckillProduct/queryByTime",{params:{time:gci.data.time}}).then((res)=>{
      gci.data.seckillProducts=res.data.data
    })
  })
},
data(){
  return{
    seckillProducts:[]
  }
},
methods:{
  makeOrder(seckillId){
    const that=this;
    axios.get("http://127.0.0.1:10010/seckill/seckillOrder/doSeckill",{params:{time:that.time,seckillId:seckillId}}).then((res)=>{
      if(res.data.statusCode==200){
        this.makeSocket();
      }
    })
  },
  makeSocket(){
    this.ws=new WebSocket('ws://127.0.0.1:10010/ws/'+localStorage.getItem("token"));
    const that=this
    this.ws.onopen =function(){};
    this.ws.onmessage=function(res){
      alert(JSON.parse(res.data).msg)
    }
    this.ws.onclose=function (){console.log("关闭连接")};
    this.ws.onerror = function(){console.log("连接已出错...");setTimeout(()=>{this.makeSocket()},1500)}
  }
}
}
</script>