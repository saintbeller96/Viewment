<template>
  <div>
      <Alert
        v-if="alert.alerted"
        :message="alert.message"
        :color="alert.color ? alert.color : 'error'"
      />
    <v-container class="py-0">
      <v-row >
            <v-text-field 
              v-model="address" 
              label='주소 검색'
              class="address-search-field mt-0 pt-5"
              height="50px"
              clearable
              clear-icon="mdi-close-circle"
              >
            </v-text-field>

            <v-card class="address-list-card">
            <v-list
              
              >
              <v-list-item
                
                
                v-for="(searchedLocation, i) in searchedLocations.slice(0,9)"
                :key="i"
                @click="searchAddress(searchedLocation)"
              >
                <v-list-item-avatar>
                <v-icon>
                  mdi-map-marker
                </v-icon>
                </v-list-item-avatar>    
                <v-list-item-content>
                  <v-list-item-title >{{searchedLocation.place_name}}</v-list-item-title>
                    <v-list-item-title >{{searchedLocation.address_name}}</v-list-item-title>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </v-card>

        
  
        <v-col cols='12' class="px-0">
        
        <div class="map_wrap">
          <div id="map" class="map" ></div>
          <div class="hAddr">
            <span id="centerAddr">{{addressName}}</span>
          </div>
        </div>
        </v-col>
    
      </v-row>
    </v-container>
  </div>
</template>

<script>
import axios from 'axios'
import Alert from '@/components/common/Alert'
const SERVER_URL = process.env.VUE_APP_SERVER_URL
export default {
  components: {
    Alert
  }, 
  data() {
    return{
      addressName: '',
      address: '',
      pinInfo: '',
      container: '',
      options: '',
      map: '',
      position: '',
      pin: '',
      coordinates: '',
      message: '',
      is_show: false,
      searchedLocations: '',
      pins: [],
      pinId: '',
      alert: {
        alerted: false,
        message: '',
        color: '',
      },
    }
  },
  mounted() {

    window.kakao && window.kakao.maps
      ? this.initMap()
      : this.addKakaoMapScript();
  },
  methods: {
    addKakaoMapScript() {
      const script = document.createElement("script");
      /* global kakao */
      script.onload = () => kakao.maps.load(this.initMap)
      script.src =
        "http://dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=41dd8e1c2fab039d8dbbff2e13e8d5a5";
      document.head.appendChild(script);
    },
    initMap() {
      const self = this
      if (navigator.geolocation) {
        self.$getLocation()
        .then(coordinates => {
          self.centerPosition = coordinates
          self.createMap()
        })
      }
      else {
        self.centerPosition = { // 센터 좌표를 직접 설정해준다. 지금은 대전 자취방 좌표
          lat : 36.3586873,
          lng : 127.30278400
        }
        self.createMap()
      }

    },
    createMap(){
      const self = this
      self.options = {
          
          center: new kakao.maps.LatLng(self.centerPosition.lat, self.centerPosition.lng),
          level: 5
        }
        self.container = document.getElementById("map")
        self.map = new kakao.maps.Map(self.container, self.options)

        
        var geocoder = new kakao.maps.services.Geocoder()
        var callback = function(result, status) {
          if (status === kakao.maps.services.Status.OK) {
              self.addressName = result[0].address_name
          }
        }
        geocoder.coord2RegionCode(self.centerPosition.lng, self.centerPosition.lat, callback)


        kakao.maps.event.addListener(self.map, 'click', function(mouseEvent) {
        self.mapClick(mouseEvent)
        })
    },
    searchAddress(res) {
      
      const self = this
      self.address = res.place_name
      var places = new kakao.maps.services.Places()

      self.coordinates = new kakao.maps.LatLng(res.y, res.x)
      self.map.setCenter(new kakao.maps.LatLng(self.coordinates.Ma, self.coordinates.La))

      // 주소 넣기
      // var geocoder = new kakao.maps.services.Geocoder()
      // var callback = function(result, status) {
      //   if (status === kakao.maps.services.Status.OK) {
      //       self.addressName = result[0].address_name
      //   }
      // }
      if (self.pinInfo !=''){
        self.pinInfo.setMap(null)
      }
      // geocoder.coord2RegionCode(self.coordinates.La, self.coordinates.Ma, callback)
      self.pin = new kakao.maps.Marker({
        map: self.map,
        position: self.coordinates
      })
      self.pinInfo = self.pin  
      
      const searchInfo = {
        lat: self.coordinates.Ma,
        lng: self.coordinates.La,
        addressName: res.address_name
      }
      this.$emit('onClick', searchInfo)
      
      self.address = ''
      self.searchedLocations = []
      
      // 클릭 이벤트
      kakao.maps.event.addListener(self.map, 'click', function(mouseEvent) {
      self.mapClick(mouseEvent)
      })
    },
    mapClick(mouseEvent) {
      const self = this
      if (self.pinInfo !=''){
        self.pinInfo.setMap(null)
      }
      var latlng = mouseEvent.latLng
      self.coordinates = new kakao.maps.LatLng(latlng.getLat(), latlng.getLng())
      self.pin = new kakao.maps.Marker({
        map: self.map,
        position: self.coordinates
      })
      self.pinInfo = self.pin      
      
      // 주소 넣기
      var geocoder = new kakao.maps.services.Geocoder()
      var callback = function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
            self.addressName = result[0].address_name
        }
      }
      geocoder.coord2RegionCode(self.coordinates.La, self.coordinates.Ma, callback)

      const clickInfo = {
        lat: this.coordinates.Ma,
        lng: this.coordinates.La,
        addressName: this.addressName
      }
      this.$emit('onClick', clickInfo)
    },
    searchLocationModal() {
      this.is_show = !this.is_show
    },
    getLocation(res) {
      if (res==null){
        this.searchedLocations= []
      }
      
      const self = this
      var places = new kakao.maps.services.Places()

      var callback = function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
          self.searchedLocations = result
        }
      };
      places.keywordSearch(res, callback);
    },
    articlePins() {   
      const self = this

      const imageSrc = require('@/assets/images/pin.png'); 
      const imageSize = new kakao.maps.Size(24, 24); 
      const pinImage = new kakao.maps.MarkerImage(imageSrc, imageSize)
      
      for (var i = 0; i < self.articles.length; i ++) {
        const position = new kakao.maps.LatLng(self.articles[i].lat, self.articles[i].lng)
        var pin = new kakao.maps.Marker({
            map: self.map, 
            position: position,
            title : self.articles[i].address_name,
            image : pinImage
        })
        
        const id = self.articles[i].pinId
        kakao.maps.event.addListener(pin, 'click', articlePinClick(id))
        this.pins.push(pin)
      }
      function articlePinClick(id) {
        return function() {
          
          if (self.pinInfo){
            self.pinInfo.setMap(null)
          }
          axios.get(`${SERVER_URL}/pins/${id}`, self.getToken)
          .then((res)=> {
            const pinInfo = {
              addressName: res.data.addressName,
              lat: res.data.lat,
              lng: res.data.lng,
              pinId: res.data.pinId
            }
            self.$emit('onClick', pinInfo)
              self.alert.message = '해당핀으로 위치 지정 완료.'
              self.alert.color = 'primary'
              self.alert.alerted = true  
          }) 
        }
      }
      var clusterer = new kakao.maps.MarkerClusterer({
      map: self.map, // 마커들을 클러스터로 관리하고 표시할 지도 객체 
      averageCenter: true, // 클러스터에 포함된 마커들의 평균 위치를 클러스터 마커 위치로 설정 
      minLevel: 8 // 클러스터 할 최소 지도 레벨 
      })
      clusterer.addMarkers(self.pins)
    },
    getArticle() {
      
      const self = this
      axios.get(`${SERVER_URL}/pins`, this.getToken)
      .then((res)=> {
        
        this.articles = res.data
        this.articlePins()
      }) 
    },    
  },
  watch: {
    address: function(res) {
      this.getLocation(res)
    }
  },
  computed: {
    getToken(){
      const token = sessionStorage.getItem('jwt')
      const config = {
        headers: {
          'X-Authorization-Firebase': token
        }
      }
      return config
    },
  },
  created() {
    this.getArticle()
  },
}
</script>

<style scoped>
.address-search-field {
  position: absolute;
  width: 100%;
  padding: 0px;
  margin: 0px;
}
.address-list-card {
  position: absolute;
  top: 50px;
  z-index: 9999;
  height:30vh;
  padding: 0;
  margin: 0;
}
.map_wrap {
  padding: 0;
  position:relative;
  width:100%;
  height:35vh;
  top: 100px;
  margin: 0;
}

.map {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 0;
}
.hAddr {
  position:absolute;
  left:0px;
  border-radius: 2px;
  background:#fff;
  background:rgba(255,255,255,0.8);
  z-index:1;
  padding:5px;
  z-index: 9000;
}
#centerAddr {
  display:block;
  /* margin-top:2px; */
  font-weight: normal;
  font-size: 10%
}

</style>