# 知识星图导航系统功能键使用说明

## 概述
知识星图导航系统提供了两个重要的功能键，用于控制3D场景的视觉效果和交互体验。

## 功能键详解

### 1. 粒子效果 🌟
**位置**: 右上角控制面板
**功能**: 控制3D场景中的星空粒子背景效果

#### 使用方法:
- **开启**: 勾选复选框，场景中将显示动态的星空粒子效果
- **关闭**: 取消勾选，粒子效果将被隐藏

#### 效果说明:
- 粒子效果包含3000个动态粒子
- 粒子具有呼吸动画效果
- 粒子颜色为青色系渐变
- 粒子会缓慢旋转，营造星空氛围

#### 技术特点:
- 使用WebGL着色器实现
- 支持透明度混合
- 具有性能优化的粒子系统

### 2. 自动旋转 🔄
**位置**: 右上角控制面板
**功能**: 控制3D场景的自动旋转效果

#### 使用方法:
- **开启**: 勾选复选框，相机将自动围绕场景中心旋转
- **关闭**: 取消勾选，相机停止自动旋转并回到默认位置

#### 效果说明:
- 相机以椭圆轨道围绕场景中心旋转
- 旋转速度适中，不会造成眩晕感
- 当选中标签时，自动旋转会暂停

#### 技术特点:
- 平滑的相机动画
- 支持用户交互暂停
- 自动重置到最佳观察位置

## 状态指示器

每个功能键旁边都有状态指示器，显示当前功能的状态：
- **开启**: 显示绿色"开启"状态
- **关闭**: 显示灰色"关闭"状态

## 设置持久化

系统会自动保存用户的偏好设置：
- 粒子效果开关状态
- 自动旋转开关状态
- 设置保存在浏览器本地存储中
- 下次访问时会自动恢复用户设置

## 性能优化

### 粒子效果优化:
- 动态创建和销毁粒子系统
- 使用GPU加速的着色器渲染
- 支持实时开关，不影响性能

### 自动旋转优化:
- 使用requestAnimationFrame实现平滑动画
- 智能暂停机制，避免不必要的计算
- 相机位置缓存，提高响应速度

## 故障排除

### 粒子效果不显示:
1. 检查浏览器是否支持WebGL
2. 确认显卡驱动是否最新
3. 尝试刷新页面重新加载

### 自动旋转不工作:
1. 检查是否有标签被选中
2. 确认功能键是否已开启
3. 尝试重新切换功能键

### 性能问题:
1. 在低端设备上建议关闭粒子效果
2. 减少浏览器标签页数量
3. 关闭其他占用GPU的应用

## 技术实现

### 粒子系统:
```javascript
// 创建粒子几何体
const geometry = new THREE.BufferGeometry()
geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))
geometry.setAttribute('size', new THREE.BufferAttribute(sizes, 1))

// 自定义着色器材质
const material = new THREE.ShaderMaterial({
  uniforms: { time: { value: 0 } },
  vertexShader: `...`,
  fragmentShader: `...`,
  blending: THREE.AdditiveBlending,
  transparent: true
})
```

### 自动旋转:
```javascript
// 相机动画循环
if (autoRotate.value && !selectedTag.value) {
  const radius = 800
  camera.position.x = Math.cos(time * 0.15) * radius
  camera.position.z = Math.sin(time * 0.15) * radius
  camera.position.y = Math.sin(time * 0.1) * 200
  camera.lookAt(0, 0, 0)
}
```

## 更新日志

### v1.0.0 (当前版本)
- ✅ 实现粒子效果开关功能
- ✅ 实现自动旋转开关功能
- ✅ 添加状态指示器
- ✅ 实现设置持久化
- ✅ 优化性能和内存管理
- ✅ 添加错误处理和调试信息

## 未来计划

### v1.1.0 (计划中)
- 🔄 添加更多粒子效果选项
- 🔄 支持自定义旋转速度
- 🔄 添加键盘快捷键支持
- 🔄 实现更多视觉效果选项

### v1.2.0 (计划中)
- 🔄 支持VR/AR模式
- 🔄 添加手势控制
- 🔄 实现多用户协作功能
- 🔄 支持自定义主题

---

**注意**: 本系统需要现代浏览器支持，建议使用Chrome、Firefox、Safari或Edge的最新版本。 