<template>
  <transition mode="out-in">
    <router-view></router-view>
  </transition>
</template>

<style>
.v-enter {
  opacity: 0;
  transform: translateX(300px);
}

.v-leave-to {
  opacity: 0;
  transform: translateX(-300px);
}

.v-enter-active,
.v-leave-active {
  transition: all 0.3s ease;
}
</style>
