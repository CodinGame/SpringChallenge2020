import * as utils from '../core/utils.js';



export class SplashAnimator {
  constructor(event, layer, module) {
    this.time = 0;
    this.duration = 600

    this.size = event.params.value == 1 ? 1 : 2

    const particleContainer = new PIXI.Container()

    particleContainer.position.set(
      event.params.x,
      event.params.y
    )
    this.particles = module.getAnimFromPool('splashParticles')
    this.particles.forEach(particle => {
      particle.speed = Math.random() * 100 + 50;
      if (this.size > 1) {
        particle.speed += 50
      }
      particleContainer.addChild(particle)
    })
    layer.addChild(particleContainer)
  }

  animate(delta) {
    this.time += delta;
    const progress = utils.unlerp(0, this.duration, this.time)

    this.particles.forEach((particle) => {
      particle.position.x = Math.cos(particle.angle) * particle.speed * progress;
      particle.position.y = Math.sin(particle.angle) * particle.speed * progress;
      particle.scale.x = particle.scale.y = this.size * (1 - progress);
      particle.alpha = (1 - progress);
    });

    if (this.time <= this.duration) {
      this.particles.busyp = false
    }
  }

  isActive() {
    return this.time <= this.duration
  }
}