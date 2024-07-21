<template>
  <div style="display:flex;flex-direction: column;">
    <div v-for="item in products" :key="item.productId" style="margin-top:20px;">
      <p>{{item.productName}}</p>
      <p>{{item.productTitle}}</p>
      <p>{{item.productPrice}}</p>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import {getCurrentInstance, onMounted} from "vue";
import qs from 'qs'
export default {
setup(){
  const gci=getCurrentInstance()
  onMounted(()=>{
    gci.data.list=[23,24,25,26]
    axios.get("http://127.0.0.1:10010/product/productQuery/selectByIdList",{params:{ids:gci.data.list},paramsSerializer:params=>{return qs.stringify(params, { indices: false })}},).then((res)=>{
      gci.data.products=res.data.data
    })
  })
},
data(){
  return{
    products:null,
  }
}
}
</script>


<style scoped>

</style>