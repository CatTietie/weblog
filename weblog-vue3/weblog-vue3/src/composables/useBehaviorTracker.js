import { ref, onMounted, onBeforeUnmount } from 'vue'
import { reportBehavior } from '@/api/frontend/recommendation'
import { getToken } from '@/composables/cookie'

const BATCH_INTERVAL = 30000
const BEHAVIOR_EVENT_TYPE = {
    READ: 1,
    TAG_CLICK: 2,
    SEARCH: 3,
    RESUME_VIEW: 4
}

export function useBehaviorTracker() {
    const eventQueue = ref([])
    let flushTimer = null
    let pageEnterTime = null

    function isLoggedIn() {
        return !!getToken()
    }

    function addEvent(event) {
        if (!isLoggedIn()) return
        eventQueue.value.push(event)
    }

    function trackRead(articleId, durationSeconds) {
        addEvent({
            eventType: BEHAVIOR_EVENT_TYPE.READ,
            articleId,
            tagId: null,
            keyword: null,
            durationSeconds: Math.round(durationSeconds)
        })
    }

    function trackTagClick(tagId) {
        addEvent({
            eventType: BEHAVIOR_EVENT_TYPE.TAG_CLICK,
            articleId: null,
            tagId,
            keyword: null,
            durationSeconds: null
        })
    }

    function trackSearch(keyword) {
        if (!keyword || !keyword.trim()) return
        addEvent({
            eventType: BEHAVIOR_EVENT_TYPE.SEARCH,
            articleId: null,
            tagId: null,
            keyword: keyword.trim(),
            durationSeconds: null
        })
    }

    function trackResumeView() {
        addEvent({
            eventType: BEHAVIOR_EVENT_TYPE.RESUME_VIEW,
            articleId: null,
            tagId: null,
            keyword: null,
            durationSeconds: null
        })
    }

    function flush(useBeacon = false) {
        if (eventQueue.value.length === 0) return
        const events = [...eventQueue.value]
        eventQueue.value = []

        if (useBeacon && navigator.sendBeacon) {
            const blob = new Blob([JSON.stringify({ events })], { type: 'application/json' })
            navigator.sendBeacon('/api/behavior/report', blob)
        } else {
            reportBehavior(events).catch(() => {})
        }
    }

    function startReadTimer() {
        pageEnterTime = Date.now()
    }

    function stopReadTimer(articleId) {
        if (pageEnterTime && articleId) {
            const duration = (Date.now() - pageEnterTime) / 1000
            if (duration >= 5) {
                trackRead(articleId, duration)
            }
        }
        pageEnterTime = null
    }

    onMounted(() => {
        flushTimer = setInterval(() => flush(false), BATCH_INTERVAL)
        window.addEventListener('beforeunload', () => flush(true))
    })

    onBeforeUnmount(() => {
        flush(false)
        if (flushTimer) {
            clearInterval(flushTimer)
            flushTimer = null
        }
        window.removeEventListener('beforeunload', () => flush(true))
    })

    return {
        trackRead,
        trackTagClick,
        trackSearch,
        trackResumeView,
        startReadTimer,
        stopReadTimer,
        flush
    }
}
