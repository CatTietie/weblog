import { ref } from 'vue'
import { useMenuStore } from '@/stores/menu'
import { useRoute, useRouter, onBeforeRouteUpdate } from 'vue-router'
import { setTabList, getTabList } from '@/composables/cookie'

export function useTabList() {
    const menuStore = useMenuStore()
    const route = useRoute()
    const router = useRouter()

    const activeTab = ref(route.path)
    const tabList = ref([
        {
            titleKey: 'admin.menu.dashboard',
            path: "/admin/index"
        },
    ])

    function addTab(tab) {
        let isTabNotExisted = tabList.value.findIndex(item => item.path == tab.path) == -1
        if (isTabNotExisted) {
            tabList.value.push(tab)
        }
        setTabList(tabList.value)
    }

    function initTabList() {
        let tabs = getTabList()
        if (tabs) {
            tabList.value = tabs
        }
    }
    initTabList()

    onBeforeRouteUpdate((to, from) => {
        activeTab.value = to.path
        addTab({
            titleKey: to.meta.titleKey,
            title: to.meta.title,
            path: to.path
        })
    })

    const tabChange = (path) => {
        activeTab.value = path
        router.push(path)
    }

    const removeTab = (path) => {
        let tabs = tabList.value
        let actTab = activeTab.value

        if (actTab == path) {
            tabs.forEach((tab, index) => {
                if (tab.path == path) {
                    let nextTab = tabs[index + 1] || tabs[index - 1]
                    if (nextTab) {
                        actTab = nextTab.path
                    }
                }
            })
        }

        activeTab.value = actTab
        tabList.value = tabList.value.filter((tab) => tab.path != path)
        setTabList(tabList.value)
    }

    const handleCloseTab = (command) => {
        let indexPath = '/admin/index'
        if (command == 'closeOthers') {
            tabList.value = tabList.value.filter((tab) => tab.path == indexPath || tab.path == activeTab.value)
        } else if (command == 'closeAll') {
            activeTab.value = indexPath
            tabList.value = tabList.value.filter((tab) => tab.path == indexPath)
        }
        setTabList(tabList.value)
    }

    return {
        menuStore,
        activeTab,
        tabList,
        tabChange,
        removeTab,
        handleCloseTab
    }
}