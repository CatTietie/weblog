<template>
    <div v-if="titles && titles.length > 0"
        class="text-sm/[30px] w-full p-5 mb-3 bg-white border border-gray-200 rounded-lg dark:bg-gray-800 dark:border-gray-700">
        <h2 class="flex items-center mb-2 font-bold text-gray-900 uppercase dark:text-white">
            <svg t="1699441758495" class="icon w-3.5 h-3.5 mr-2" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="4043" width="200" height="200">
                <path
                    d="M857.6 25.6a76.8 76.8 0 0 1 76.8 76.8v819.2a76.8 76.8 0 0 1-76.8 76.8H166.4a76.8 76.8 0 0 1-76.8-76.8V102.4a76.8 76.8 0 0 1 76.8-76.8h691.2z m-102.4 678.4H473.6l-2.2528 0.064a38.4 38.4 0 0 0 0 76.672L473.6 780.8h281.6l2.2528-0.064a38.4 38.4 0 0 0 0-76.672L755.2 704z m0-230.4H473.6l-2.2528 0.064a38.4 38.4 0 0 0 0 76.672L473.6 550.4h281.6l2.2528-0.064a38.4 38.4 0 0 0 0-76.672L755.2 473.6z m0-230.4H473.6l-2.2528 0.064a38.4 38.4 0 0 0 0 76.672L473.6 320h281.6l2.2528-0.064a38.4 38.4 0 0 0 0-76.672L755.2 243.2z"
                    fill="#6B57FE" p-id="4044"></path>
                <path
                    d="M281.6 691.2a51.2 51.2 0 1 1 0 102.4 51.2 51.2 0 0 1 0-102.4z m0-230.4a51.2 51.2 0 1 1 0 102.4 51.2 51.2 0 0 1 0-102.4z m0-230.4a51.2 51.2 0 1 1 0 102.4 51.2 51.2 0 0 1 0-102.4z"
                    fill="#FFBA00" p-id="4045"></path>
            </svg>
            文章目录
        </h2>
        <div class="border-l-2 border-gray-200 toc-container">
            <ul>
                <li v-for="(h2, index) in titles" :key="index" class="toc-item pl-5"
                    :class="{ 'toc-item-active': h2.index === activeHeadingIndex }">
                    <span @click="scrollToHeading(h2)" class="toc-link hover:text-sky-600 cursor-pointer">{{ h2.text }}</span>
                    <ul v-if="h2.children && h2.children.length > 0">
                        <li v-for="(h3, index2) in h2.children" :key="index2" class="toc-item pl-5"
                            :class="{ 'toc-item-active': h3.index === activeHeadingIndex }">
                            <span @click="scrollToHeading(h3)" class="toc-link hover:text-sky-600 cursor-pointer">{{ h3.text }}</span>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'

const titles = ref([])
const activeHeadingIndex = ref(-1)
const observer = ref(null)
const isScrollListenerAdded = ref(false)

function getAbsoluteOffsetTop(element) {
    let offsetTop = 0
    while (element) {
        offsetTop += element.offsetTop
        element = element.offsetParent
    }
    return offsetTop - 95
}

function initTocData(container) {
    let levels = ['h2', 'h3']
    let headings = container.querySelectorAll(levels)

    if (!headings || headings.length === 0) {
        titles.value = []
        return
    }

    let titlesArr = []
    let index = 1

    headings.forEach(heading => {
        let headingLevel = parseInt(heading.tagName.substring(1))
        let headingText = heading.innerText
        let offsetTop = getAbsoluteOffsetTop(heading)

        heading.setAttribute('data-toc-index', index)
        heading.setAttribute('data-toc-offset', offsetTop)

        if (headingLevel === 2) {
            titlesArr.push({
                index,
                level: headingLevel,
                text: headingText,
                offsetTop,
                element: heading,
                children: []
            })
        } else {
            if (titlesArr.length > 0) {
                let parentHeading = titlesArr[titlesArr.length - 1]
                parentHeading.children.push({
                    index,
                    level: headingLevel,
                    text: headingText,
                    offsetTop,
                    element: heading
                })
            }
        }
        index++
    })

    titles.value = titlesArr
}

function getAllHeadings() {
    const allHeadings = []
    titles.value.forEach(h2 => {
        allHeadings.push(h2)
        if (h2.children && h2.children.length > 0) {
            h2.children.forEach(h3 => {
                allHeadings.push(h3)
            })
        }
    })
    return allHeadings.sort((a, b) => a.index - b.index)
}

function handleContentScroll() {
    let scrollY = window.scrollY
    const allHeadings = getAllHeadings()

    if (allHeadings.length === 0) {
        activeHeadingIndex.value = -1
        return
    }

    let currentActive = -1

    for (let i = 0; i < allHeadings.length; i++) {
        const heading = allHeadings[i]
        if (scrollY >= heading.offsetTop) {
            currentActive = heading.index
        } else {
            break
        }
    }

    activeHeadingIndex.value = currentActive
}

function scrollToHeading(heading) {
    if (heading && heading.element) {
        heading.element.scrollIntoView({ behavior: 'smooth', block: 'start' })
        window.scrollBy(0, -95)
    } else if (heading && typeof heading.offsetTop !== 'undefined') {
        window.scrollTo({ top: heading.offsetTop, behavior: 'smooth' })
    }
}

function waitForContainer() {
    return new Promise((resolve) => {
        const checkContainer = () => {
            const container = document.querySelector('.article-content')
            if (container) {
                resolve(container)
            } else {
                requestAnimationFrame(checkContainer)
            }
        }
        checkContainer()
    })
}

onMounted(async () => {
    const container = await waitForContainer()

    observer.value = new MutationObserver((mutationsList) => {
        for (let mutation of mutationsList) {
            if (mutation.type === 'childList') {
                nextTick(() => {
                    initTocData(container)

                    const images = container.querySelectorAll('img')
                    images.forEach(img => {
                        const handleImageLoad = () => {
                            initTocData(container)
                            handleContentScroll()
                        }
                        if (img.complete) {
                            handleImageLoad()
                        } else {
                            img.addEventListener('load', handleImageLoad, { once: true })
                        }
                    })

                    if (!isScrollListenerAdded.value) {
                        window.addEventListener('scroll', handleContentScroll)
                        isScrollListenerAdded.value = true
                    }

                    handleContentScroll()
                })
            }
        }
    })

    const config = { childList: true, subtree: true, characterData: true }
    observer.value.observe(container, config)

    if (container.innerHTML && container.innerHTML.trim() !== '') {
        nextTick(() => {
            initTocData(container)
            if (!isScrollListenerAdded.value) {
                window.addEventListener('scroll', handleContentScroll)
                isScrollListenerAdded.value = true
            }
            handleContentScroll()
        })
    }
})

onBeforeUnmount(() => {
    if (observer.value) {
        observer.value.disconnect()
    }
    if (isScrollListenerAdded.value) {
        window.removeEventListener('scroll', handleContentScroll)
        isScrollListenerAdded.value = false
    }
})
</script>

<style scoped>
.toc-container {
    position: relative;
}

.toc-item {
    position: relative;
    transition: all 0.2s ease;
}

.toc-item::before {
    content: '';
    position: absolute;
    left: -2px;
    top: 0;
    bottom: 0;
    width: 2px;
    background-color: transparent;
    transition: background-color 0.2s ease;
}

.toc-item-active {
    font-weight: 600;
}

.toc-item-active::before {
    background-color: #0ea5e9;
}

.toc-item-active .toc-link {
    color: #0ea5e9;
}

.toc-link {
    display: block;
    color: #6b7280;
    transition: color 0.2s ease;
    line-height: 2;
}

.toc-link:hover {
    color: #0ea5e9;
}
</style>
