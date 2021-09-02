// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sectorTesting;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.sectors.LoadedSectorUpdateEvent;
import org.terasology.engine.entitySystem.sectors.SectorSimulationComponent;
import org.terasology.engine.entitySystem.sectors.SectorSimulationEvent;
import org.terasology.engine.entitySystem.sectors.SectorUtil;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.chunks.event.BeforeChunkUnload;
import org.terasology.engine.world.chunks.event.OnChunkLoaded;

import java.util.HashSet;
import java.util.Set;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TestSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(TestSystem.class);

    @In
    private EntityManager entityManager;

    @Override
    public void postBegin() {
        //Creating sector-scope entity with TestComponent for simulation
        //Set the entity to simulate every 10 seconds when unloaded, and every second when loaded
        EntityRef entity = entityManager.createSectorEntity(10000, 1000);
        entity.addComponent(new TestComponent());

        //Add a SectorRegionComponent to set the watched chunks
        Set<Vector3i> chunks = new HashSet<>();
        //Add the chunk with position 10, 0, 0
        chunks.add(new Vector3i(10, 0, 0));
        //Add the chunk that contains the block with position 1000, 0, 1000
        chunks.add(Chunks.toChunkPos(new Vector3i(1000, 0, 1000)));
        entity.addComponent(SectorUtil.createSectorRegionComponent(chunks));

        //Set the location to 0, 0, 0
        //This is not needed, but will add the chunk containing this block to be watched, too
        entity.addComponent(new LocationComponent(new Vector3f(0, 0, 0)));

    }

    @ReceiveEvent
    public void simulateCounting(SectorSimulationEvent event, EntityRef entity, TestComponent component) {
        //Simulation only. This is the only place the value changes
        component.floatValue += event.getDelta() * 2;
        logger.info("Entity {} simulating. Value is {}", entity.getId(), component.floatValue);
    }

    @ReceiveEvent
    public void echoCounting(LoadedSectorUpdateEvent event, EntityRef entity, TestComponent component) {
        //Where changes to the world would be made
        //This is only called while the chunk is loaded
        logger.info("Entity {} loaded. Value is {}", entity.getId(), component.floatValue);
    }

    @ReceiveEvent(components = SectorSimulationComponent.class)
    public void chunkLoaded(OnChunkLoaded event, EntityRef entity) {
        //Called once when the chunk loads
        logger.info("Chunk load: entity {}", entity.getId());
    }

    @ReceiveEvent(components = SectorSimulationComponent.class)
    public void chunkUnloaded(BeforeChunkUnload event, EntityRef entity) {
        //Called once when the chunk unloads
        logger.info("Chunk unload: entity {}", entity.getId());
    }

}
