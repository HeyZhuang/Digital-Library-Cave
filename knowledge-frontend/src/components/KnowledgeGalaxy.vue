<template>
  <div ref="galaxyContainer" class="fixed inset-0 z-0 opacity-80"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as THREE from 'three'

interface Props {
  particleCount?: number
  isDarkMode?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  particleCount: 1000,
  isDarkMode: false
})

const galaxyContainer = ref<HTMLElement>()
let scene: THREE.Scene
let camera: THREE.PerspectiveCamera
let renderer: THREE.WebGLRenderer
let particles: THREE.Points
let animationId: number

// 创建知识星云
const createGalaxy = () => {
  scene = new THREE.Scene()
  
  // 设置相机
  camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000)
  camera.position.z = 50
  
  // 设置渲染器
  renderer = new THREE.WebGLRenderer({ 
    alpha: true, 
    antialias: true,
    powerPreference: "high-performance"
  })
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  
  // 创建粒子几何体
  const geometry = new THREE.BufferGeometry()
  const positions = new Float32Array(props.particleCount * 3)
  const colors = new Float32Array(props.particleCount * 3)
  const sizes = new Float32Array(props.particleCount)
  
  // 金色系颜色组合
  const goldColors = [
    new THREE.Color(0xd4af37), // 鎏金
    new THREE.Color(0xf9e076), // 浅金
    new THREE.Color(0xffd700), // 黄金
    new THREE.Color(0xffb300), // 琥珀
    new THREE.Color(0xffeaa7), // 金沙
  ]
  
  // 生成星云形状的粒子分布
  for (let i = 0; i < props.particleCount; i++) {
    // 螺旋星系形状
    const radius = Math.random() * 100
    const spinAngle = radius * 0.1
    const branchAngle = (i % 3) * (Math.PI * 2 / 3)
    
    const randomX = Math.pow(Math.random(), 0.75) * (Math.random() < 0.5 ? 1 : -1) * 0.3 * radius
    const randomY = Math.pow(Math.random(), 0.75) * (Math.random() < 0.5 ? 1 : -1) * 0.3 * radius
    const randomZ = Math.pow(Math.random(), 0.75) * (Math.random() < 0.5 ? 1 : -1) * 0.3 * radius
    
    positions[i * 3] = Math.cos(branchAngle + spinAngle) * radius + randomX
    positions[i * 3 + 1] = randomY
    positions[i * 3 + 2] = Math.sin(branchAngle + spinAngle) * radius + randomZ
    
    // 根据距离中心的距离设置颜色
    const distanceToCenter = Math.sqrt(
      positions[i * 3] ** 2 + 
      positions[i * 3 + 1] ** 2 + 
      positions[i * 3 + 2] ** 2
    )
    
    const colorIndex = Math.floor((distanceToCenter / 100) * goldColors.length)
    const color = goldColors[Math.min(colorIndex, goldColors.length - 1)]
    
    colors[i * 3] = color.r
    colors[i * 3 + 1] = color.g
    colors[i * 3 + 2] = color.b
    
    // 设置粒子大小
    sizes[i] = Math.random() * 3 + 1
  }
  
  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))
  geometry.setAttribute('size', new THREE.BufferAttribute(sizes, 1))
  
  // 创建着色器材质
  const material = new THREE.ShaderMaterial({
    uniforms: {
      time: { value: 0 },
      pixelRatio: { value: Math.min(window.devicePixelRatio, 2) }
    },
    vertexShader: `
      uniform float time;
      uniform float pixelRatio;
      attribute float size;
      varying vec3 vColor;
      
      void main() {
        vColor = color;
        
        vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
        
        // 添加微妙的浮动效果
        mvPosition.xyz += sin(time * 0.001 + position.x * 0.01) * 0.1;
        
        gl_Position = projectionMatrix * mvPosition;
        gl_PointSize = size * pixelRatio * (100.0 / -mvPosition.z);
      }
    `,
    fragmentShader: `
      varying vec3 vColor;
      
      void main() {
        float distanceToCenter = distance(gl_PointCoord, vec2(0.5));
        float strength = 1.0 - smoothstep(0.0, 0.5, distanceToCenter);
        
        // 创建发光效果
        float glow = 1.0 - smoothstep(0.0, 0.3, distanceToCenter);
        
        gl_FragColor = vec4(vColor, strength * 0.8);
        gl_FragColor.rgb += vColor * glow * 0.2;
      }
    `,
    transparent: true,
    vertexColors: true,
    blending: THREE.AdditiveBlending,
    depthWrite: false
  })
  
  particles = new THREE.Points(geometry, material)
  scene.add(particles)
  
  // 添加环境光
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.1)
  scene.add(ambientLight)
  
  // 添加点光源
  const pointLight = new THREE.PointLight(0xd4af37, 0.5, 100)
  pointLight.position.set(0, 0, 30)
  scene.add(pointLight)
}

// 动画循环
const animate = () => {
  animationId = requestAnimationFrame(animate)
  
  // 旋转星系
  if (particles) {
    particles.rotation.y += 0.001
    particles.rotation.x += 0.0005
    
    // 更新时间uniform
    if (particles.material instanceof THREE.ShaderMaterial) {
      particles.material.uniforms.time.value = Date.now()
    }
  }
  
  renderer.render(scene, camera)
}

// 处理窗口大小变化
const handleResize = () => {
  if (!camera || !renderer) return
  
  camera.aspect = window.innerWidth / window.innerHeight
  camera.updateProjectionMatrix()
  renderer.setSize(window.innerWidth, window.innerHeight)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
}

// 响应主题变化
watch(() => props.isDarkMode, (newValue) => {
  if (particles && particles.material instanceof THREE.ShaderMaterial) {
    // 根据主题调整透明度
    particles.material.opacity = newValue ? 0.6 : 0.4
  }
})

onMounted(() => {
  if (!galaxyContainer.value) return
  
  createGalaxy()
  galaxyContainer.value.appendChild(renderer.domElement)
  animate()
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  
  window.removeEventListener('resize', handleResize)
  
  // 清理Three.js资源
  if (particles) {
    particles.geometry.dispose()
    if (particles.material instanceof THREE.Material) {
      particles.material.dispose()
    }
  }
  
  if (renderer) {
    renderer.dispose()
  }
})
</script>

<style scoped>
/* 确保背景层级正确 */
div {
  pointer-events: none;
}
</style> 