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
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.internal.EntityInfoComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.protobuf.EntityData;
import org.terasology.registry.In;

import java.util.ArrayList;
import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TestSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(TestSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private Context context;

    @Override
    public void update(float delta) {

        for (EntityRef entity : entityManager.getAllEntities()) {
            if (entity == null) {
                logger.error("Entity is null");
                continue;
            } else if (entity == EntityRef.NULL) {
                logger.error("Entity is EntityRef.NULL");
            }
            //logger.info("Entity {} has scope {}", entity.getId(), entity.getScope());
            //logger.info("Component? {}", entity.getComponent(TestComponent.class));

            //logger.info("==== scope {}", entity.getScope());
            TestComponent comp = entity.getComponent(TestComponent.class);
            if (comp != null) {
                logger.info("==== {}, scope {}", comp.testValue, entity.getScope());
            }
        }

        List<Class<? extends Component>> l = new ArrayList<>();
        l.add(TestComponent.class);

        for (EntityRef entity : entityManager.getEntitiesWith(TestComponent.class)) {
            TestComponent component = entity.getComponent(TestComponent.class);
            component.testValue++;
            logger.info("{}: Entity {}", component.testValue, entity.getId());
        }
    }

    @ReceiveEvent
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player) {
        //SectorManager sectorManager = entityManager.getSectorManager();

        //Global entity
        EntityRef entity = entityManager.create();
        TestComponent testComp = new TestComponent();
        testComp.testValue = 0;
        entity.addComponent(testComp);
        logger.info("Created {} entity with id ", entity.getId());

        //entityManager.getSectorPool().createEntityWithId(entity.getId(), new ArrayList<Component>());
        //entityManager.getSectorPool().create(entityManager.getPrefabManager().getPrefab("noprefab"));


        //Sector entity
        EntityRef entity2 = entityManager.createSectorEntity();
        TestComponent testComp2 = new TestComponent();
        testComp2.testValue = -10000;
        entity2.addComponent(testComp2);
        logger.info("Created {} entity with id ", entity2.getId());


        /*
                sectorManager.moveToPool(entity.getId(), sectorManager);
        logger.info("Are the entities in the same sector? Should be true: " +
                sectorManager.inSameSector(entity.getId(), entity2.getId()));

        EntityRef entity3 = entityManager.createSectorEntity();

        logger.info("Are the entities in the same sector? Should be true: " +
                sectorManager.inSameSector(entity3.getId(), entity2.getId()));
                */
    }
}
