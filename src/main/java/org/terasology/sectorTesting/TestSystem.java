/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.sectorTesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.logic.players.PlayerCharacterComponent;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.registry.In;

import java.util.Random;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TestSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(TestSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private Context context;

    private int timer = 0;

    @Override
    public void update(float delta) {

        for (EntityRef entity : entityManager.getAllEntities()) {
            if (entity == null) {
                logger.error("Entity is null");
                continue;
            } else if (entity == EntityRef.NULL) {
                logger.error("Entity is EntityRef.NULL");
            }

            TestComponent comp = entity.getComponent(TestComponent.class);
            if (comp != null) {
                logger.info("== val {} scp {} aw: {} act: {}", comp.testValue, entity.getScope(), entity.isAlwaysRelevant(), entity.isActive());
                LocationComponent loc = entity.getComponent(LocationComponent.class);
                if (loc != null) {
                    logger.info("id: {}; x: {} y: {} z: {}", entity.getId(), loc.getWorldPosition().x, loc.getWorldPosition().y,
                            loc.getWorldPosition().z);
                }
            }
        }
        logger.info("==========");

        if ((timer++ % 200) == 0 && timer <= 1000) {
            for (EntityRef player : entityManager.getEntitiesWith(PlayerCharacterComponent.class)) {
                LocationComponent loc;
                if ((loc = player.getComponent(LocationComponent.class)) != null) {
                    Random rand = new Random();
                    EntityRef e;
                    if (rand.nextBoolean()) {
                        e = entityManager.create();
                    } else {
                        e = entityManager.createSectorEntity();
                    }
                    e.addComponent(new LocationComponent(loc.getWorldPosition()));
                    e.addComponent(new TestComponent());
                    e.setAlwaysRelevant(true);
                }
            }
        }

    }

    @ReceiveEvent
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player) {

        //Global entity
        EntityRef entity = entityManager.create();
        TestComponent testComp = new TestComponent();
        testComp.testValue = 0;
        entity.addComponent(testComp);
        logger.info("Created {} entity with id ", entity.getId());

        //Sector entity
        EntityRef entity2 = entityManager.createSectorEntity();
        TestComponent testComp2 = new TestComponent();
        testComp2.testValue = 1000;
        entity2.addComponent(testComp2);
        logger.info("Created {} entity with id ", entity2.getId());
    }

    @Override
    public void initialise() {

    }

}
