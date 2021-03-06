import Login from './views/user/Login.vue'
import Join from './views/user/Join.vue'
import ConfirmEmail from './views/user/ConfirmEmail.vue'
import Error from './views/error/Error.vue'
import NotFound from './views/error/NotFound.vue'
import ChangePassword from './views/user/ChangePassword.vue'
import NewsFeed from './views/newsfeed/NewsFeed.vue'
import ActionUrlHandler from './views/user/ActionUrlHandler.vue'
import Search from './views/search/Search.vue'
import Profile from './views/user/Profile.vue'
import FollowList from './views/user/FollowList.vue'
import MemoryList from './views/memory/MemoryList.vue'
import EditProfile from './views/user/EditProfile.vue'
import EditAccount from './views/user/EditAccount.vue'
import CreateArticle from './views/article/CreateArticle.vue'
import DetailArticle from './views/article/DetailArticle.vue'
import UpdateArticle from './views/article/UpdateArticle.vue'
import BindArticle from './views/article/BindArticle.vue'
import SearchHashtagGrid from './views/search/SearchHashtagGrid.vue'
import Curation from './views/curation/Curation.vue'
import NotiList from './views/noti/NotiList.vue'
import NotiSettings from './views/user/NotiSettings.vue'

export default [
    {
        path : '/',
        name : 'Login',
        component : Login,
        props : true,
        meta : {title: 'Viewment - 로그인'},
    },
    {
        path : '/user/join/',
        name : 'Join',
        meta : {title: 'Viewment - 회원가입'},
        component : Join,
    },
    {
        path : '/newsfeed/main',
        name : 'NewsFeed',
        meta : {title: 'Viewment - 피드'},
        component : NewsFeed
    },
    {
        path : '/user/find',
        name : 'ConfirmEmail',
        meta : {title: 'Viewment - 비밀번호 재설정'},
        component : ConfirmEmail
    },
    {
        path : '/user/error',
        name : 'Error',
        component : Error
    },
    {
        path : '/user/error/404',
        name : 'NotFound',
        component : NotFound
    },
    {
        path : '/user/reset-password',
        name : 'ChangePassword',
        component : ChangePassword
    },
    {
        path : '/user/action-url-handler',
        name : 'ActionUrlHandler',
        component : ActionUrlHandler,
    },
    {
        path : '/user/:profileUserId',
        name : 'Profile',
        component : Profile,
        meta : { title: 'Viewment - 프로필'},
        props : true,
    },
    {
        path : '/user/profile/:profileUserId/follow-list',
        name : 'FollowList',
        component : FollowList,
        props : true,
    },
    {
        path : '/memory/:profileUserId/memory-list',
        name : 'MemoryList',
        component : MemoryList,
        props : true,
    },
    {
        path : '/user/profile/edit',
        name : 'EditProfile',
        component : EditProfile,
    },
    {
        path : '/user/account/edit',
        name : 'EditAccount',
        component : EditAccount,
    },
    {
        path: '*',
        redirect: '/user/error/404'
    },
    {
        path : '/search/search',
        name : 'Search',
        meta : { title: 'Viewment - 검색'},
        component : Search,
    },
    {
        path : '/article/create',
        name : 'CreateArticle',
        meta : { title: 'Viewment - 게시물 생성'},
        component : CreateArticle,
    }, 
    {
        path : '/article/:articleId',
        name : 'DetailArticle',
        meta : { title: 'Viewment - 게시글 상세'},
        component : DetailArticle,
        props: true,
    }, 
    {
        path : '/article/update/:articleId',
        name : 'UpdateArticle',
        meta : { title: 'Viewment - 게시글 수정'},
        component : UpdateArticle,
    }, 
    {
        path : '/pin/:pinId',
        name : 'BindArticle',
        component : BindArticle,
    }, 
    {
        path : '/curation',
        name : 'Curation',
        meta : { title: 'Viewment - 게시물 추천'},
        component : Curation,
    },  
    {
        path : '/search/search-hashtag-grid/:clickedHash',
        name : 'SearchHashtagGrid',
        meta : { title: 'Viewment - 검색'},
        component : SearchHashtagGrid,
        props: true,
    },  
    {
        path : '/noti-list',
        name : 'NotiList',
        component : NotiList,
    },
    {
        path : '/user/profile/noti',
        name : 'NotiSettings',
        component : NotiSettings,
    },
]
