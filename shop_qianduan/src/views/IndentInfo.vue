<template>
    <div style="display:flex;flex-direction:column;">
      <div style="display:flex;">
        <p>订单编号:</p><p>{{indent.orderNo}}</p>
      </div>
      <div style="display:flex;">
        <p>订单状态:</p><p>{{indent.status}}</p>
      </div>
      <div style="display:flex;">
        <p>订单创建时间:</p><p>{{indent.createDate}}</p>
      </div>
      <div style="display:flex;">
        <p>订单支付时间:</p><p>{{indent.payDate}}</p>
      </div>
      <div style="display:flex;">
        <p>订单支付方式:</p><p>{{indent.payType}}</p>
      </div>

      <button @click="refund(indent.orderNo)">退款</button>
    </div>
</template>

<script>
import {getCurrentInstance, onMounted, ref} from "vue";
import axios from "axios";
import router from "@/router/First.js";

export default {
setup(){
  const gci=getCurrentInstance()
  onMounted(()=>{
    gci.data.orderNo=router.currentRoute.value.query.orderNo
    axios.get("http://127.0.0.1:10010/indent/search/findIndentById",{params:{orderNo:gci.data.orderNo}}).then((res)=>{
      gci.data.indent=res.data.data
    })
  })
},
data(){
  return{
    indent:""
  }
},
methods:{
  refund(orderNo){
    axios.get("http://127.0.0.1:10010/indent/execute/refundForIndent",{params:{orderNo:orderNo}}).then((res)=>{
      console.log(res.data.data)
    })
  }
}
}
</script>
