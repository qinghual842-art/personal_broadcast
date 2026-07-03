import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/PublicLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('@/views/public/home/index.vue') },
      { path: 'article/:slugOrId', name: 'ArticleDetail', component: () => import('@/views/public/article-detail/index.vue') },
      { path: 'search', name: 'Search', component: () => import('@/views/public/article-search/index.vue') },
      { path: 'category/:id', name: 'Category', component: () => import('@/views/public/category/index.vue') },
      { path: 'tag/:id', name: 'Tag', component: () => import('@/views/public/tag/index.vue') },
      { path: 'categories', name: 'CategoryList', component: () => import('@/views/public/category-list/index.vue') },
      { path: 'tags', name: 'TagList', component: () => import('@/views/public/tag-list/index.vue') },
      { path: 'archive', name: 'Archive', component: () => import('@/views/public/archive/index.vue') },
      { path: 'about', name: 'Statement', component: () => import('@/views/public/about/index.vue') },
      { path: 'agents', name: 'AgentList', component: () => import('@/views/public/agent-list/index.vue') },
      { path: 'agents/:id/chat', name: 'AgentChat', component: () => import('@/views/public/agent-chat/index.vue') },
      { path: 'profile', name: 'UserProfile', component: () => import('@/views/public/profile/index.vue') },
      { path: 'my-articles', name: 'MyArticles', component: () => import('@/views/public/my-articles/index.vue') },
      { path: 'editor', name: 'Editor', component: () => import('@/views/public/editor/index.vue') },
      { path: 'my-agents/create', name: 'UserAgentEditor', component: () => import('@/views/public/my-agents/editor.vue') },
    ]
  },
  // User login
  { path: '/login', name: 'UserLogin', component: () => import('@/views/public/user-login/index.vue') },
  // Admin login
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/login/index.vue')
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/admin/dashboard/index.vue') },
      { path: 'articles', name: 'ArticleList', component: () => import('@/views/admin/article/index.vue') },
      { path: 'articles/create', name: 'ArticleCreate', component: () => import('@/views/admin/article/editor.vue') },
      { path: 'articles/:id/edit', name: 'ArticleEdit', component: () => import('@/views/admin/article/editor.vue') },
      { path: 'categories', name: 'CategoryManage', component: () => import('@/views/admin/category/index.vue') },
      { path: 'tags', name: 'TagManage', component: () => import('@/views/admin/tag/index.vue') },
      { path: 'comments', name: 'CommentManage', component: () => import('@/views/admin/comment/index.vue') },
      { path: 'agents', name: 'AgentListManage', component: () => import('@/views/admin/agent/index.vue') },
      { path: 'agents/create', name: 'AgentCreate', component: () => import('@/views/admin/agent/editor.vue') },
      { path: 'agents/:id/edit', name: 'AgentEdit', component: () => import('@/views/admin/agent/editor.vue') },
      { path: 'site', name: 'SiteSettings', component: () => import('@/views/admin/site/index.vue') },
      { path: 'profile', name: 'Profile', component: () => import('@/views/admin/profile/index.vue') },
    ]
  },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: () => import('@/views/NotFound.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/admin/login')
  } else {
    next()
  }
})

export default router
