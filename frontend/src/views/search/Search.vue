<template>
<v-row
    justify="center"
  >
    <v-col
      lg="4"
      md="4"
      sm="6"
    >

    <v-text-field
    class="mx-2 mt-5"
    rows="1"
    @input="onSearch"
    clearable
    single-line
    >
    <!-- prepend-inner-icon="mdi-magnify" -->
      <template 
        v-slot:label
      >
      <v-icon>mdi-magnify</v-icon>
        {{searchLabel}}
      </template>

    </v-text-field>
  <!-- 탭 -->
  <v-row >

    <v-tabs
      style="z-index: 10;"
      v-model="onTab"
      grow
    >
      <!-- v-slide-group__prev -->
      <!-- $slide-group-prev-basis=52px -->
      <v-tab
        v-for="tabItem in tabItems"
        :key="tabItem.tabId"
      >
        <v-icon>{{ tabItem.icon }}</v-icon>
      </v-tab>
    </v-tabs>
  </v-row>
  <!-- 탭 -->

  <!-- 탭-컴포넌트 -->
  <v-tabs-items
    v-model="onTab"
  >
    <v-tab-item
      v-for="tabItem in tabItems"
      :key="tabItem.tabId"
    >
      <v-card flat>
        <component 
          v-bind:is="tabItem.content"
          :search="search"
          :onTab="onTab"
        ></component>
      </v-card>
    </v-tab-item>
  </v-tabs-items>
    <!-- 탭-컴포넌트 -->

   </v-col>
 </v-row>
</template>

<script>
import SearchLocation from '@/components/search/SearchLocation'
import SearchHashtag from '@/components/search/SearchHashtag'
import SearchUser from '@/components/search/SearchUser'
import SearchRecord from '@/components/search/SearchRecord'

export default {
  name: 'Search',
  components: {
    SearchRecord,
    SearchLocation,   
    SearchHashtag,
    SearchUser,   
  },
  data: () => {
    return {
      searchLabel: "Search",
      search: null,
      onTab: null,
      tabItems: [
        { tabId: 10, tabName: 'record', icon: 'mdi-text-box-search', content: 'SearchRecord', searchLabel: "Search"},
        { tabId: 11, tabName: 'aMap', icon: 'mdi-map-marker', content: 'SearchLocation', searchLabel: "Map Search"},
        { tabId: 22, tabName: 'Feesdafd', icon: 'mdi-pound', content: 'SearchHashtag', searchLabel: "Hashtag Search"},
        { tabId: 33, tabName: 'Useasdfr', icon: 'mdi-account', content: 'SearchUser', searchLabel: "User Search"}
      ],
    }
  },
  methods: {
    onSearch(res) {
      this.search = res
    }
  },
}
</script>

<style>

</style>