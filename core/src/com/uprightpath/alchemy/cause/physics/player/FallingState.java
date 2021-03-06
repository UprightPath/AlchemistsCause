package com.uprightpath.alchemy.cause.physics.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.uprightpath.alchemy.cause.control.Control;
import com.uprightpath.alchemy.cause.physics.AgentEntity;
import com.uprightpath.alchemy.cause.physics.PhysicsWorld;

/**
 * Created by Geo on 8/31/2014.
 */
public class FallingState extends PlayerState {
    protected Polygon collisionPolygon;
    protected Polygon platformCollisionPolygon;
    protected Vector2 previousVelocity = new Vector2();
    protected Vector2 movement = new Vector2(.005f, 1f / 300f);
    protected Vector2 maximum = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
    protected Vector2 minimum = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);

    public FallingState() {
        this.collisionPolygon = new Polygon(new float[]{0.f, 1.8f, 0.f, 0.f, .8f, 0.f, .8f, 1.8f});
        this.platformCollisionPolygon = new Polygon(new float[]{0f, 0f, 0.f, -.1f, .8f, -.1f, .8f, 0f});
    }

    public FallingState(PlayerAgent playerAgent) {
        super(playerAgent);
        this.collisionPolygon = new Polygon(new float[]{0.f, 1.8f, 0.f, 0.f, .8f, 0.f, .8f, 1.8f});
        this.platformCollisionPolygon = new Polygon(new float[]{0f, 0f, 0.f, -.1f, .8f, -.1f, .8f, 0f});
    }

    @Override
    public void applyLogic(PhysicsWorld physicsWorld) {
        previousVelocity.set(playerAgent.getVelocity());
        if (Control.JUMP.isDown() && playerAgent.getVelocity().y > 0) {
            playerAgent.getAcceleration().y = movement.y;
        }
        if (!Control.DOWN.isDown()) {
            playerAgent.setFallThrough(false);
        }
        if (Control.LEFT.isDown()) {
            playerAgent.getAcceleration().x = -movement.x;
        } else if (Control.RIGHT.isDown()) {
            playerAgent.getAcceleration().x = movement.x;
        }
        if (!Control.DOWN.isDown()) {
            playerAgent.setFallThrough(false);
        }
        playerAgent.applyDeltaVelocity(playerAgent.getAcceleration());
    }

    @Override
    public void applyLimits(PhysicsWorld physicsWorld) {
        if (!(Control.LEFT.isDown() || Control.RIGHT.isDown())) {
            this.playerAgent.getVelocity().x = this.playerAgent.getVelocity().x * physicsWorld.getAirFriction().x;
        }
        float maxX = Math.max(Math.abs(previousVelocity.x * physicsWorld.getAirFriction().x), Math.min(physicsWorld.getAirSpeedMax().x, maximum.x));
        float maxY = Math.max(Math.abs(previousVelocity.y), Math.min(physicsWorld.getAirSpeedMax().y, maximum.y));
        playerAgent.getVelocity().x = MathUtils.clamp(playerAgent.getVelocity().x, -maxX, maxX);
        playerAgent.getVelocity().y = MathUtils.clamp(playerAgent.getVelocity().y, -maxY, maxY);
        if (!(Control.LEFT.isDown() || Control.RIGHT.isDown()) && Math.abs(playerAgent.getVelocity().x) <= Math.min(physicsWorld.getAirSpeedMin().x, minimum.x)) {
            playerAgent.getVelocity().x = 0;
        }
    }

    @Override
    protected void updatePosition() {
        collisionPolygon.setPosition(playerAgent.getPosition().x, playerAgent.getPosition().y);
        platformCollisionPolygon.setPosition(playerAgent.getPosition().x, playerAgent.getPosition().y);
    }

    @Override
    public Polygon getCollisionPolygon() {
        return collisionPolygon;
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public Polygon getPlatformCollisionPolygon() {
        return platformCollisionPolygon;
    }

    @Override
    public void collidedWithAgent(AgentEntity agentEntity) {

    }
}
