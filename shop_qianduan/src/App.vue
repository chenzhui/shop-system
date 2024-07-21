<template style="width:100%">
  <div style="display:flex;flex-direction:column;width:100%">
    <div style="display:flex;flex-direction:row;width:100%">
      <router-link to="/Register" style="width:20%">注册</router-link>
      <router-link to="/Login" style="width:20%">登录</router-link>
      <router-link to="/Seckill" style="width:20%">秒杀</router-link>
      <router-link to="/Index" style="width:20%">首页</router-link>
      <router-link to="/Indent" style="width:20%">订单</router-link>

    </div>
    <router-view></router-view>
  </div>

</template>
<script>

</script>

<style scoped>
</style>

