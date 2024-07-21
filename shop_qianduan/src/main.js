import { createApp } from 'vue'
import App from './App.vue'
import router from "@/router/First.js";
import axios from "axios";
import VueCookies from "vue-cookies";
const app=createApp(App)
axios.defaults.baseUrl=""
axios.interceptors.request.use(config=>{
    config.headers.token=window.localStorage.getItem("token")
    return config
})
app.use(router)
app.use(VueCookies)
app.mount('#app')
