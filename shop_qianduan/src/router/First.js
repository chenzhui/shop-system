import {createRouter, createWebHashHistory} from "vue-router";
const routes=[
    {path:'/',redirect:'/Index'},
    {path: '/Index', component: () => import('../views/Index.vue'),},
    {path: '/Register', component: () => import('../views/Register.vue'),},
    {path: '/Login', component: () => import('../views/Login.vue'),},
    {path: '/Seckill', component: () => import('../views/Seckiil.vue'),},
    {path: '/Indent', component: () => import('../views/Indent.vue'),},
    {path: '/IndentInfo', component: () => import('../views/IndentInfo.vue'),},


]
const router=createRouter({
    history:createWebHashHistory(),
    routes,
})
export default router
