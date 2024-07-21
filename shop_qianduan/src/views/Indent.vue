<template>
  <div v-for="item in indents" :key="item.orderNo" style="display:flex;">
    <p>{{item.orderNo}}</p>
    <button @click="payIndent(item.orderNo)">支付宝支付</button>
    <button @click="PayIndentIntergral(item.orderNo)">积分支付</button>
    <button @click="RefundIndent(item.orderNo)">退款</button>
  </div>
<div ref="payPage" style="height:200px;width:100%"></div>


</template>

<script>
import {getCurrentInstance, onMounted, ref} from "vue";
import axios from "axios";

export default {
setup(){
  const gci=getCurrentInstance()
  let payPage=ref(null)
  onMounted(()=>{
    axios.get("http://127.0.0.1:10010/indent/search/findMyIndents").then((res)=>{
      gci.data.indents=res.data.data
    })
  })
  return{
    payPage
  }
},
data(){
  return{
    indents:[]
  }
},
methods:{
  payIndent(orderNo){
    const that=this
    axios.get("http://127.0.0.1:10010/indent/execute/payForIndent",{params:{orderNo:orderNo,payType:0}}).then((res)=>{
      const div = document.createElement('formdiv');
      div.innerHTML = res.data.data;
      document.body.appendChild(div);
      document.forms[0].setAttribute('target', '_self');
      document.forms[0].submit();
      div.remove();

    })
  },
  PayIndentIntergral(orderNo){
    axios.get("http://127.0.0.1:10010/indent/execute/payForIndent",{params:{orderNo:orderNo,payType:1}}).then((res)=>{
      console.log(res)
    })
  },
  RefundIndent(orderNo){
    axios.get("http://127.0.0.1:10010/indent/execute/refundForIndent",{params:{orderNo:orderNo}}).then((res)=>{
      console.log(res)
    })
  },
}
}
</script>

<style scoped>

</style>